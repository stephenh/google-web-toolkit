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
package com.google.gwt.sample.showcase.client.content.lists;

import com.google.gwt.i18n.client.Constants;
import com.google.gwt.sample.showcase.client.ContentWidget;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Example file.
 * 
 * @gwt.CSS .gwt-ListBox
 */
public class CwListBox extends ContentWidget {
  /**
   * The constants used in this Content Widget.
   * 
   * @gwt.SRC
   */
  public static interface CwConstants extends Constants,
      ContentWidget.CwConstants {
    String[] cwListBoxCategories();

    String cwListBoxDescription();

    String cwListBoxName();

    String cwListBoxSelectAll();

    String cwListBoxSelectCategory();

    String[] cwListBoxSports();

    String[] cwListBoxVacations();
  }

  /**
   * The data for each type of list.
   * 
   * @gwt.DATA
   */
  private static final String[] carTypes = {
      "Acura", "Audi", "BMW", "Buick", "Chevrolet", "Dodge", "Ford", "Honda",
      "KIA", "Lexus", "Lincoln", "Mercedes", "Porsche", "Saturn", "Toyota",
      "Volkswagen", "Volvo"};

  /**
   * An instance of the constants.
   * 
   * @gwt.DATA
   */
  private CwConstants constants;

  /**
   * Constructor.
   * 
   * @param constants the constants
   */
  public CwListBox(CwConstants constants) {
    super(constants);
    this.constants = constants;
  }

  @Override
  public String getDescription() {
    return constants.cwListBoxDescription();
  }

  @Override
  public String getName() {
    return constants.cwListBoxName();
  }

  /**
   * Initialize this example.
   * 
   * @gwt.SRC
   */
  @Override
  public Widget onInitialize() {
    // Create a panel to align the Widgets
    HorizontalPanel hPanel = new HorizontalPanel();
    hPanel.setSpacing(20);

    // Add a drop box with the list types
    final ListBox dropBox = new ListBox(false);
    String[] listTypes = constants.cwListBoxCategories();
    for (int i = 0; i < listTypes.length; i++) {
      dropBox.addItem(listTypes[i]);
    }
    dropBox.ensureDebugId("cwListBox-dropBox");
    VerticalPanel dropBoxPanel = new VerticalPanel();
    dropBoxPanel.setSpacing(4);
    dropBoxPanel.add(new HTML(constants.cwListBoxSelectCategory()));
    dropBoxPanel.add(dropBox);
    hPanel.add(dropBoxPanel);

    // Add a list box with multiple selection enabled
    final ListBox multiBox = new ListBox(true);
    multiBox.ensureDebugId("cwListBox-multiBox");
    multiBox.setWidth("11em");
    multiBox.setVisibleItemCount(10);
    VerticalPanel multiBoxPanel = new VerticalPanel();
    multiBoxPanel.setSpacing(4);
    multiBoxPanel.add(new HTML(constants.cwListBoxSelectAll()));
    multiBoxPanel.add(multiBox);
    hPanel.add(multiBoxPanel);

    // Add a listener to handle drop box events
    dropBox.addChangeListener(new ChangeListener() {
      public void onChange(Widget sender) {
        showCategory(multiBox, dropBox.getSelectedIndex());
        multiBox.ensureDebugId("cwListBox-multiBox");
      }
    });

    // Show default category
    showCategory(multiBox, 0);
    multiBox.ensureDebugId("cwListBox-multiBox");

    // Return the panel
    return hPanel;
  }

  /**
   * Display the options for a given category in the list box.
   * 
   * @param listBox the ListBox to add the options to
   * @param category the category index
   * @gwt.SRC
   */
  private void showCategory(ListBox listBox, int category) {
    listBox.clear();
    String[] listData = null;
    switch (category) {
      case 0:
        listData = carTypes;
        break;
      case 1:
        listData = constants.cwListBoxSports();
        break;
      case 2:
        listData = constants.cwListBoxVacations();
        break;
    }
    for (int i = 0; i < listData.length; i++) {
      listBox.addItem(listData[i]);
    }
  }
}
