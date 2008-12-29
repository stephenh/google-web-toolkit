/*
 * Copyright 2008 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.gwt.soyc;

import org.xml.sax.*;
import org.xml.sax.helpers.*;
import org.xml.sax.SAXException;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeSet;
import java.util.TreeMap;
import java.util.zip.GZIPInputStream;
import javax.xml.parsers.*;

public class SoycDashboard {
  
  /**
   * @param args Input: XML file containing soyc compile output
   * @throws IOException 
   * @throws SAXException 
   */
  public static void main(String[] args) {
    
    if (args.length != 2){
      System.err.println("Usage: java com/google/gwt/soyc/SoycDashboard soyc-report0.xml[.gz] soyc-dependencies0.xml[.gz]");
      System.exit(1);
    }
    
    String inFileName = args[0];
    String depInFileName = args[1];
    
    /**
     * handle dependencies
     */

    Map<String, ArrayList<String>> dependencies = new TreeMap<String, ArrayList<String>>();
    DefaultHandler depHandler = parseXMLDocumentDependencies(dependencies);
    
    // start parsing
    SAXParserFactory depFactoryMain = SAXParserFactory.newInstance();
    depFactoryMain.setNamespaceAware(true);
    try {
      SAXParser saxParser = depFactoryMain.newSAXParser();
      InputStream in = new FileInputStream(depInFileName);
      if (depInFileName.endsWith(".gz")) {
        in = new GZIPInputStream(in);
      }
      in = new BufferedInputStream(in);
      saxParser.parse(in,depHandler);
    } catch (ParserConfigurationException e) {
      throw new RuntimeException("Could not parse document. ", e);
    } catch (SAXException e) {
      throw new RuntimeException("Could not create SAX parser. ", e);
    } catch (FileNotFoundException e) {
      throw new RuntimeException("Could not open file. ", e);
    } catch (IOException e) {
      throw new RuntimeException("Could not open file. ", e);
    }
    
    try{
      MakeTopLevelHtmlForPerm.makeDependenciesHtml(dependencies);
    } catch (IOException e) {
      throw new RuntimeException("Cannot open file. ", e);
    }

    
    /**
     * handle everything else
     */

    // to store literals data
    GlobalInformation.nameToLitColl.put("long",new LiteralsCollection("long"));
    GlobalInformation.nameToLitColl.put("null",new LiteralsCollection("null"));
    GlobalInformation.nameToLitColl.put("class",new LiteralsCollection("class"));
    GlobalInformation.nameToLitColl.put("int",new LiteralsCollection("int"));
    GlobalInformation.nameToLitColl.put("string",new LiteralsCollection("string"));
    GlobalInformation.nameToLitColl.put("number",new LiteralsCollection("number"));
    GlobalInformation.nameToLitColl.put("boolean",new LiteralsCollection("boolean"));
    GlobalInformation.nameToLitColl.put("double",new LiteralsCollection("double"));
    GlobalInformation.nameToLitColl.put("char",new LiteralsCollection("char"));
    GlobalInformation.nameToLitColl.put("undefined",new LiteralsCollection("undefined"));
    GlobalInformation.nameToLitColl.put("float",new LiteralsCollection("float"));
    
    // to store code data
    GlobalInformation.nameToCodeColl.put("allOther", new CodeCollection("allOther"));
    GlobalInformation.nameToCodeColl.put("widget", new CodeCollection("widget"));
    GlobalInformation.nameToCodeColl.put("rpcUser", new CodeCollection("rpcUser"));
    GlobalInformation.nameToCodeColl.put("rpcGen", new CodeCollection("rpcGen"));
    GlobalInformation.nameToCodeColl.put("rpcGwt", new CodeCollection("rpcGwt"));
    GlobalInformation.nameToCodeColl.put("gwtLang", new CodeCollection("long"));
    GlobalInformation.nameToCodeColl.put("jre", new CodeCollection("jre"));
    
    
    // make the parser handler
    DefaultHandler handler = parseXMLDocument(GlobalInformation.nameToLitColl, GlobalInformation.nameToCodeColl);

    
    // start parsing
    SAXParserFactory factoryMain = SAXParserFactory.newInstance();
    factoryMain.setNamespaceAware(true);
    try {
      SAXParser saxParser = factoryMain.newSAXParser();
      InputStream in = new FileInputStream(inFileName);
      if (inFileName.endsWith(".gz")) {
        in = new GZIPInputStream(in);
      }
      in = new BufferedInputStream(in);
      saxParser.parse(in,handler);
    } catch (ParserConfigurationException e) {
      throw new RuntimeException("Could not parse document. ", e);
    } catch (SAXException e) {
      throw new RuntimeException("Could not create SAX parser. ", e);
    } catch (FileNotFoundException e) {
      throw new RuntimeException("Could not open file. ", e);
    } catch (IOException e) {
      throw new RuntimeException("Could not open file. ", e);
    }

    // add to "All Other Code" if none of the special categories apply
    updateAllOtherCodeType(GlobalInformation.nameToCodeColl);
    
    // now we need to aggregate numbers
    GlobalInformation.computePackageSizes();
    GlobalInformation.computePartialPackageSizes();
    
    // clean up the RPC categories
    foldInRPCHeuristic(GlobalInformation.nameToCodeColl);
    
    // generate all the html files
    makeHTMLFiles(GlobalInformation.nameToLitColl, GlobalInformation.nameToCodeColl); 
    
    System.out.println("Finished creating reports. To see the dashboard, open SoycDashboard-index.html in your browser.");
  }


  private static DefaultHandler parseXMLDocument(
      final TreeMap<String, LiteralsCollection> nameToLitColl,
      final HashMap<String, CodeCollection> nameToCodeColl) {
    
    DefaultHandler handler = new DefaultHandler() {

      String curStoryId;
      String curStoryLiteralType;
      String curLineNumber;
      String curLocation;
      String curStoryRef;
      HashSet<String> curRelevantLitTypes = new HashSet<String>();
      HashSet<String> curRelevantCodeTypes = new HashSet<String>();
      String curClassId;
      String curPackage;
      Integer curFragment;
      String curFunctionId = "";
      boolean specialCodeType = false;
      StringBuilder valueBuilder = new StringBuilder();
      int ct = 0;
      boolean fragmentInLoadOrder = false;
      
      /**
       * This method deals with the beginning of the XML element.
       * It analyzes the XML node and adds its information to the relevant literal or code collection for later analysis.
       * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
       */
      @Override
      public void startElement(String nsUri, String strippedName, String tagName, Attributes attributes) {
        
        if ((ct % 10000) == 0){
          System.out.println(ct);
        }
        ct++;
        
        valueBuilder.delete(0,valueBuilder.length());
        
        if (strippedName.compareTo("splitpoint") == 0){
          System.out.println("about to process splitpoint");
         parseSplitPoint(attributes); 
        }
        else if ((strippedName.compareTo("package") == 0)&&(attributes.getValue("id") != null)){
          curPackage = attributes.getValue("id");
          
          if (curPackage.compareTo("") == 0){
            curPackage = "emptyPackageName";
          }
          
          if (! GlobalInformation.packageToClasses.containsKey(curPackage)){
            TreeSet<String> insertSet = new TreeSet<String>();
            GlobalInformation.packageToClasses.put(curPackage, insertSet);
          }
        }
        
        else if (strippedName.compareTo("class") == 0){
          
          parseClass(nameToCodeColl, attributes);
        }

        else if (strippedName.compareTo("function") == 0){
          if (attributes.getValue("id") != null){
            curFunctionId = attributes.getValue("id");
          }
        }
        
        //TODO(kprobst): not currently used, but will be for dependencies
        else if (strippedName.compareTo("on") == 0){
          parseDependsOn(nameToCodeColl, attributes);
        }
        
        else if (strippedName.compareTo("of") == 0){
          parseOverrides(nameToCodeColl, attributes);
        }

        else if (strippedName.compareTo("by") == 0){
          parseCorrelations(nameToCodeColl, attributes);
        }

        else if (strippedName.compareTo("story") == 0){
          parseStory(attributes);
        }
        
        else if (strippedName.compareTo("origin") == 0){
          parseOrigins(nameToLitColl, attributes);
        }
        
        else if (strippedName.compareTo("js") == 0){
          if (attributes.getValue("fragment") != null){
            // ignore all code that is not in the first load order
            
            curFragment = Integer.parseInt(attributes.getValue("fragment"));
            if((curFragment == 0)||
                (curFragment == (GlobalInformation.numSplitPoints+1))||
                (curFragment == (GlobalInformation.numSplitPoints+2))||
                ((curFragment >= 2) && (curFragment <= GlobalInformation.numSplitPoints))){
              fragmentInLoadOrder = true;
            }
            else{
              fragmentInLoadOrder = false;
            }
          }
          else{
           
            curFragment = -2;
          }
        }

        else if (strippedName.compareTo("storyref") == 0){
          parseJs(nameToLitColl, nameToCodeColl, attributes, curFragment);
        }
      }

      
      /**
       * This method collects a block of the value of the current XML node that the SAX parser parses.
       * It simply adds to the the previous blocks, so that we can collect the entire value block.
       */
       @Override
      public void characters (char ch[], int start, int length){
         valueBuilder.append(ch, start, length);
       }
      

     /**
      * This method marks the end of an XML element that the SAX parser parses.
      * It has access to the full value of the node and uses it add information to the relevant literal or code collections.
      * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
      */
      @Override
      public void endElement (String  nsUri, String strippedName, String qName){
        String value = valueBuilder.toString();
        
        int numBytes = value.getBytes().length;
          
        if (curStoryRef != null){
          
          //add this size to the current fragment
          if (GlobalInformation.storiesToCorrClasses.containsKey(curStoryRef)){
            
            if ((GlobalInformation.storiesToLitType.containsKey(curStoryRef))&&(GlobalInformation.storiesToCorrClasses.get(curStoryRef).size() > 0)){
             GlobalInformation.numBytesDoubleCounted += numBytes;
            }
            
            float partialSize = (float)numBytes / (float)GlobalInformation.storiesToCorrClasses.get(curStoryRef).size();                               
            if (!GlobalInformation.fragmentToPartialSize.containsKey(curFragment)){
              GlobalInformation.fragmentToPartialSize.put(curFragment, partialSize);
            }
            else{
              float newSize = GlobalInformation.fragmentToPartialSize.get(curFragment) + partialSize;
              GlobalInformation.fragmentToPartialSize.put(curFragment, newSize);
            }
                    
            // now do different things depending on whether this fragment is in the load order or not
            if (fragmentInLoadOrder == false){
              GlobalInformation.allOtherFragmentsPartialSize += numBytes;
            }
            else{
              GlobalInformation.cumSizeAllCode += numBytes;
              
              if ((!GlobalInformation.storiesToLitType.containsKey(curStoryRef))&&
                  (!GlobalInformation.storiesToCorrClasses.containsKey(curStoryRef))){
                 GlobalInformation.nonAttributedStories.add(curStoryRef);
                 GlobalInformation.nonAttributedBytes += numBytes;
              }
            
            
              // go through all the classes for this story
              for (String className : GlobalInformation.storiesToCorrClasses.get(curStoryRef)){
                // get the corresponding package
                if (GlobalInformation.classToPackage.containsKey(className)){
                  String packageName = GlobalInformation.classToPackage.get(className);
                  
                  if (!GlobalInformation.packageToClasses.containsKey(packageName)){
                    TreeSet<String> insertSet = new TreeSet<String>();
                    insertSet.add(className);
                    GlobalInformation.packageToClasses.put(packageName, insertSet);
                  }
                  else{
                    GlobalInformation.packageToClasses.get(packageName).add(className);
                  }
                  
                  if (GlobalInformation.classToSize.containsKey(className)){
                    int newSize = GlobalInformation.classToSize.get(className) + numBytes;
                    GlobalInformation.classToSize.put(className, newSize);
                  }
                  else{
                    GlobalInformation.classToSize.put(className, numBytes);  
                  }
                  
                  if (GlobalInformation.classToPartialSize.containsKey(className)){
                    float newSize = GlobalInformation.classToPartialSize.get(className) + partialSize;
                    GlobalInformation.classToPartialSize.put(className, newSize);
                  }
                  else{
                    GlobalInformation.classToPartialSize.put(className, partialSize);  
                  }
                }
              }
            }
          }
          updateLitTypes(nameToLitColl, value, numBytes);    
        }
      }

      
      /*
       * This method assigns strings to the appropriate category
       */
      private void updateLitTypes(
          final TreeMap<String, LiteralsCollection> nameToLitColl,
          String value, int numBytes) {
        
        int iNumCounted = 0;
        
        for (String relLitType : curRelevantLitTypes){
          iNumCounted++;
          
          //then give string literals special treatment
          if (relLitType.compareTo("string") == 0){
            
            // note that this will double-count (i.e., it will count a string twice if it's in the output twice), as it should.
            nameToLitColl.get("string").cumStringSize += numBytes;
            nameToLitColl.get(relLitType).cumSize += numBytes;
                       
            //get the origins
            HashSet<String> originSet = nameToLitColl.get("string").storyToLocations.get(curStoryRef);
            
            // find the most appropriate string literal category
            String mostAppropriateCategory = "";
            String mostAppropriateLocation = "";
            String backupLocation = "";
            for (String origin : originSet){
              
              if ((origin.contains("ClassLiteralHolder")) && (mostAppropriateCategory.compareTo("") == 0)){
                mostAppropriateCategory = "compiler";
                mostAppropriateLocation = origin;
              }
              else if ((origin.startsWith("transient source for"))&&(origin.contains("_TypeSerializer")) && (mostAppropriateCategory.compareTo("") == 0)){
                mostAppropriateCategory = "transient";
                mostAppropriateLocation = origin;
              }
              else if ((origin.contains("InlineResourceBundleGenerator")) && (mostAppropriateCategory.compareTo("") == 0)){
                mostAppropriateCategory = "inlinedTextRes";
                mostAppropriateLocation = origin;
              }
              if (origin.compareTo("com.google.gwt.dev.js.ast.JsProgram: Line 0") != 0){
                backupLocation = origin;
              }
            }

            if (backupLocation.compareTo("") == 0){
              backupLocation = "com.google.gwt.dev.js.ast.JsProgram: Line 0";
            }
            if ((((value.startsWith("'")) && (value.endsWith("'"))) ||
                ((value.startsWith("\""))&&(value.endsWith("\"")))) &&
                (mostAppropriateCategory.compareTo("") == 0)){
              mostAppropriateCategory = "user";
              mostAppropriateLocation = backupLocation;
            }
            else if (mostAppropriateCategory.compareTo("") == 0){
              mostAppropriateCategory = "otherStrings";
              mostAppropriateLocation = backupLocation;
            }
                        
            if (!nameToLitColl.get("string").stringLiteralToType.containsKey(value)){
              nameToLitColl.get("string").stringLiteralToType.put(value, mostAppropriateCategory);
              if (!nameToLitColl.get("string").stringTypeToCount.containsKey(mostAppropriateCategory)){
                   nameToLitColl.get("string").stringTypeToCount.put(mostAppropriateCategory, 1); 
              }
              else{
                int iNewCount = nameToLitColl.get("string").stringTypeToCount.get(mostAppropriateCategory) + 1; 
                nameToLitColl.get("string").stringTypeToCount.put(mostAppropriateCategory, iNewCount); 
              }

              
              int iNewSize = numBytes;
              if (nameToLitColl.get("string").stringTypeToSize.containsKey(mostAppropriateCategory)){
                iNewSize += nameToLitColl.get("string").stringTypeToSize.get(mostAppropriateCategory);
              }
              nameToLitColl.get("string").stringTypeToSize.put(mostAppropriateCategory, iNewSize);


              if (nameToLitColl.get("string").storyToLocations.containsKey(curStoryRef)){
                HashSet<String> insertSet = new HashSet<String>();
                insertSet.add(mostAppropriateLocation);
                nameToLitColl.get(relLitType).literalToLocations.put(value,insertSet);
              }
            } 
          }

          else{
            // note that this will double-count (i.e., it will count a literal twice if it's in the output twice), as it should.
            nameToLitColl.get(relLitType).cumSize += numBytes;
            
            if (nameToLitColl.get(relLitType).storyToLocations.containsKey(curStoryRef)){
              if (nameToLitColl.get(relLitType).literalToLocations.containsKey(value)){
                nameToLitColl.get(relLitType).literalToLocations.get(value).addAll(nameToLitColl.get(relLitType).
                      storyToLocations.get(curStoryRef));
              }
              else{
                HashSet<String> insertSet = nameToLitColl.get(relLitType).storyToLocations.get(curStoryRef);
                nameToLitColl.get(relLitType).literalToLocations.put(value,insertSet);
              }
              
            }
          }          
        }
      }
      
      /* 
       * parses the "JS" portion of the XML file
       */
      private void parseJs(
          final TreeMap<String, LiteralsCollection> nameToLitColl,
          final HashMap<String, CodeCollection> nameToCodeColl,
          Attributes attributes, Integer curFragment) {
        curRelevantLitTypes.clear();
        curRelevantCodeTypes.clear();
        
        if (attributes.getValue("idref") != null){
          
          curStoryRef = attributes.getValue("idref");
          
          if (curFragment != -1){
            //add this to the stories for this fragment
            if (!GlobalInformation.fragmentToStories.containsKey(curFragment)){
              HashSet<String> insertSet = new HashSet<String>();
              insertSet.add(curStoryRef);
              GlobalInformation.fragmentToStories.put(curFragment, insertSet);
            }
            else{
              GlobalInformation.fragmentToStories.get(curFragment).add(curStoryRef);
            }
          }
          
          for (String litType : nameToLitColl.keySet()){
            if (nameToLitColl.get(litType).storyToLocations.containsKey(curStoryRef)){
              curRelevantLitTypes.add(litType);
            }
          }
          
          specialCodeType = false;
          for (String codeType : nameToCodeColl.keySet()){
            if (nameToCodeColl.get(codeType).stories.contains(curStoryRef)){
              curRelevantCodeTypes.add(codeType);
              specialCodeType = true;
            }
          }
          if (specialCodeType == false){
                      
              nameToCodeColl.get("allOther").stories.add(curStoryRef);
            curRelevantCodeTypes.add("allOther");
          }
        }
      }

      /*
       * parses the "origins" portion of the XML file
       */
      private void parseOrigins(
          final TreeMap<String, LiteralsCollection> nameToLitColl,
          Attributes attributes) {
        if ((curStoryLiteralType.compareTo("") != 0)&&
            (attributes.getValue("lineNumber") != null)&&
            (attributes.getValue("location") != null)){
          curLineNumber = attributes.getValue("lineNumber");
          curLocation = attributes.getValue("location");
          String curOrigin = curLocation + ": Line " + curLineNumber;
          
          if (!nameToLitColl.get(curStoryLiteralType).storyToLocations.containsKey(curStoryId)){
            HashSet<String> insertSet = new HashSet<String>();
            insertSet.add(curOrigin);
            nameToLitColl.get(curStoryLiteralType).storyToLocations.put(curStoryId, insertSet);
          }
          else{
            nameToLitColl.get(curStoryLiteralType).storyToLocations.get(curStoryId).add(curOrigin);
          }
        }
      }

      /*
       * parses the split points
       */
      private void parseSplitPoint(Attributes attributes){
        System.out.println("processing split point");
        if (attributes.getValue("id") != null){
          String curSplitPoint = attributes.getValue("id");
          System.out.println("\tcurSplitPoint is: " + curSplitPoint);
          if (attributes.getValue("location") != null){
            String curSplitPointLocation = attributes.getValue("location");
            
            curSplitPointLocation = curSplitPointLocation.replaceAll("\\(L.*","");
            
            System.out.println("\tcurSplitPointLocation is: " + curSplitPointLocation);
            GlobalInformation.splitPointToLocation.put(Integer.parseInt(curSplitPoint), curSplitPointLocation);
            GlobalInformation.numSplitPoints++;
            
            System.out.println("adding split point and location: " + curSplitPoint + "-->" + curSplitPointLocation);
            System.out.println("\t(Number of split points is now: " + GlobalInformation.numSplitPoints + ")");
            
          }
        }
      }
      
      /*
       * parses the "story" portion of the XML file
       */
      private void parseStory(Attributes attributes) {
        if (attributes.getValue("id") != null){
          curStoryId = attributes.getValue("id");
          if (attributes.getValue("literal") != null){
            curStoryLiteralType = attributes.getValue("literal");
            GlobalInformation.storiesToLitType.put(curStoryId, curStoryLiteralType);
            

            if (!nameToLitColl.get(curStoryLiteralType).storyToLocations.containsKey(curStoryId)){
              HashSet<String> insertSet = new HashSet<String>();
              nameToLitColl.get(curStoryLiteralType).storyToLocations.put(curStoryId, insertSet);
            }
            
          }
          else{
            curStoryLiteralType = "";
          }
        }
      }

      /*
       * parses the "correlations" portion of the XML file
       */
      private void parseCorrelations(
          final HashMap<String, CodeCollection> nameToCodeColl,
          Attributes attributes) {
        
        if (attributes.getValue("idref") != null){
          
          String corrClassOrMethod = attributes.getValue("idref");
          
          String corrClass = attributes.getValue("idref");
 
          if (corrClass.contains(":")){
            corrClass = corrClass.replaceAll(":.*", "");
          }
          
          if (GlobalInformation.classToPackage.containsKey(corrClass)){ //if we know about this class
            
            if (! GlobalInformation.storiesToCorrClassesAndMethods.containsKey(curStoryId)){
              HashSet<String> insertSet = new HashSet<String>();
              insertSet.add(corrClassOrMethod);
              GlobalInformation.storiesToCorrClassesAndMethods.put(curStoryId, insertSet);
            }
            else{
              GlobalInformation.storiesToCorrClassesAndMethods.get(curStoryId).add(corrClassOrMethod);
            }
            
  
            if (! GlobalInformation.storiesToCorrClasses.containsKey(curStoryId)){
              HashSet<String> insertSet = new HashSet<String>();
              insertSet.add(corrClass);
              GlobalInformation.storiesToCorrClasses.put(curStoryId, insertSet);
            }
            else{
              GlobalInformation.storiesToCorrClasses.get(curStoryId).add(corrClass);
            }
            
            for (String codeType : nameToCodeColl.keySet()){
              if (nameToCodeColl.get(codeType).classes.contains(corrClass)){
                nameToCodeColl.get(codeType).stories.add(curStoryId);
              }
            }
          }
        }
      }

      /*
       * parses the "overrides" portion of the XML file
       */
      private void parseOverrides(
          final HashMap<String, CodeCollection> nameToCodeColl,
          Attributes attributes) {
        if (attributes.getValue("idref") != null){
          String overriddenClass = attributes.getValue("idref");
          

          //TODO(kprobst): fix this -- we either generalize to classes, or the numbers are messed up...
          if (overriddenClass.contains(":")){
            overriddenClass = overriddenClass.replaceAll(":.*", "");
          }
          
          if (overriddenClass.compareTo("com.google.gwt.user.client.ui.UIObject") == 0){
            nameToCodeColl.get("widget").classes.add(curClassId);
          }
          else if (overriddenClass.contains("java.io.Serializable") || 
                   overriddenClass.contains("IsSerializable")){
            nameToCodeColl.get("rpcUser").classes.add(curClassId);
          }
          else if (overriddenClass.contains("com.google.gwt.user.client.rpc.core.java")){
            nameToCodeColl.get("rpcGwt").classes.add(curClassId);
          }
        }
      }

      /*
       * parses the "class" portion of the XML file
       */
      private void parseClass(
          final HashMap<String, CodeCollection> nameToCodeColl,
          Attributes attributes) {
        if (attributes.getValue("id") != null){
          curClassId = attributes.getValue("id");

          GlobalInformation.classToPackage.put(curClassId, curPackage);

          if (curPackage.startsWith("java")){
            nameToCodeColl.get("jre").classes.add(curClassId);
          }
          else if (curPackage.startsWith("com.google.gwt.lang")){
            nameToCodeColl.get("gwtLang").classes.add(curClassId);
          }
          if (curClassId.contains("_CustomFieldSerializer")){
            nameToCodeColl.get("rpcUser").classes.add(curClassId);
          }
          else if (curClassId.endsWith("_FieldSerializer") || 
                    curClassId.endsWith("_Proxy") ||
                    curClassId.endsWith("_TypeSerializer")){
            nameToCodeColl.get("rpcGen").classes.add(curClassId);
          }
        }
      }
      
      /*
       * parses the "depends on" portion of the XML file
       */
      private void parseDependsOn(final HashMap<String, CodeCollection> nameToCodeColl, Attributes attributes) {
        if (curFunctionId.compareTo("") == 0){
          if (attributes.getValue("idref") != null){
            String curDepClassId = attributes.getValue("idref");
  
            if (curDepClassId.contains(":")){
              // strip everything after the :: (to get to class, even if it's a method)
              curDepClassId = curDepClassId.replaceAll(":.*", "");
            }
 
            if (curDepClassId.contains(".")){
              if (! GlobalInformation.classToWhatItDependsOn.containsKey(curClassId)){
                HashSet<String> insertSet = new HashSet<String>();
                insertSet.add(curDepClassId);
                GlobalInformation.classToWhatItDependsOn.put(curClassId, insertSet);
              }
              else{
                GlobalInformation.classToWhatItDependsOn.get(curClassId).add(curDepClassId);
              }
            }
          }
        }
      }
    };
    return handler;
  }
  

  
  
  


  private static DefaultHandler parseXMLDocumentDependencies(final Map<String, ArrayList<String>> dependencies) { 
    
    DefaultHandler handler = new DefaultHandler() {

      StringBuilder valueBuilder = new StringBuilder();
      // may want to create a class for this later
      String curMethod;
        
      /**
       * This method deals with the beginning of the XML element.
       * It analyzes the XML node and adds its information to the relevant literal or code collection for later analysis.
       * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
       */
      @Override
      public void startElement(String nsUri, String strippedName, String tagName, Attributes attributes) {
      
        valueBuilder.delete(0,valueBuilder.length());
        
        
        if ((strippedName.compareTo("method") == 0)&&(attributes.getValue("name") != null)){
          curMethod = attributes.getValue("name");
        }
        else if ((strippedName.compareTo("called") == 0)&&(attributes.getValue("by") != null)){
          String curDepMethod = attributes.getValue("by");
          if (!dependencies.containsKey(curMethod)){
            ArrayList<String> insertArray = new ArrayList<String>();
            insertArray.add(curDepMethod);
            dependencies.put(curMethod, insertArray);
          }
          else{
            dependencies.get(curMethod).add(curDepMethod);
          }
        }
      }

    };
    return handler;
  }
  

  
  
  
  /*
   * cleans up the RPC code categories
   */
  private static void foldInRPCHeuristic(
      final HashMap<String, CodeCollection> nameToCodeColl) {
    /**
     * Heuristic: this moves all classes that override serializable from RPC to "Other Code" *if* there is no RPC generated code, i.e., if the 
     * application really is not using RPC
     */
    
  
    if (nameToCodeColl.get("rpcGen").classes.size() == 0){
      
      for (String className : nameToCodeColl.get("rpcUser").classes){
        
        if ((! nameToCodeColl.get("widget").classes.contains(className))&&
            (! nameToCodeColl.get("jre").classes.contains(className))&&
            (! nameToCodeColl.get("gwtLang").classes.contains(className))){
          nameToCodeColl.get("allOther").classes.add(className);          
        } 
      }
      nameToCodeColl.get("rpcUser").classes.clear();
      
      for (String className : nameToCodeColl.get("rpcGwt").classes){        
        if ((! nameToCodeColl.get("widget").classes.contains(className))&&
          (! nameToCodeColl.get("jre").classes.contains(className))&&
          (! nameToCodeColl.get("gwtLang").classes.contains(className))){
          nameToCodeColl.get("allOther").classes.add(className);          
        } 
  
      }
      nameToCodeColl.get("rpcGwt").classes.clear();
    }
            
  
  }


  /*
   * generates all the HTML files
   */
  private static void makeHTMLFiles(
      final TreeMap<String, LiteralsCollection> nameToLitColl,
      final HashMap<String, CodeCollection> nameToCodeColl) {

    try {
      MakeTopLevelHtmlForPerm.makePackageClassesHtmls();
      MakeTopLevelHtmlForPerm.makeCodeTypeClassesHtmls(nameToCodeColl);
      MakeTopLevelHtmlForPerm.makeLiteralsClassesTableHtmls(nameToLitColl);
      MakeTopLevelHtmlForPerm.makeStringLiteralsClassesTableHtmls(nameToLitColl);
      // MakeTopLevelHtmlForPerm.makeFragmentClassesHtmls();
      MakeTopLevelHtmlForPerm.makeSplitPointClassesHtmls();
      MakeTopLevelHtmlForPerm.makeDependenciesTableHtmls();

      //make the shell last so we can display aggregate information here
      MakeTopLevelHtmlForPerm.makeHTMLShell(nameToCodeColl, nameToLitColl);
      
    } catch (IOException e) {
      throw new RuntimeException("Cannot open file. ", e);
    }
  }

  
  /*
   * assigns code to "all other code" if none of the special categories apply
   */
  private static void updateAllOtherCodeType(final HashMap<String, CodeCollection> nameToCodeColl){
    //all classes not in any of the other categories
    for (String className : GlobalInformation.classToPackage.keySet()){
      if ( (!nameToCodeColl.get("widget").classes.contains(className))&&
          (!nameToCodeColl.get("rpcUser").classes.contains(className))&&
          (!nameToCodeColl.get("rpcGwt").classes.contains(className))&&
          (!nameToCodeColl.get("rpcGen").classes.contains(className))&&
          (!nameToCodeColl.get("jre").classes.contains(className))&&
          (!nameToCodeColl.get("gwtLang").classes.contains(className))){
        nameToCodeColl.get("allOther").classes.add(className);
      }
    }
  }


  /*
   * unescape the JS snippets - in the XML file they are XML encoded for correct display, but this 
   * will mess up the byte counts
   */
  public static String unEscapeXml(String escaped) {
    String unescaped = escaped.replaceAll("&amp;","\\&");
    unescaped = unescaped.replaceAll("&lt;","\\<");
    unescaped = unescaped.replaceAll("&gt;","\\>");
    unescaped = unescaped.replaceAll("&quot;","\\\"");
    //escaped = escaped.replaceAll("\\n", "");
    unescaped = unescaped.replaceAll("&apos;","\\'");
    return unescaped;
  }
  

  
  
}





