/*
 * Copyright 2010 Google Inc.
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
package com.google.gwt.cell.client;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment.VerticalAlignmentConstant;

import java.util.Set;

/**
 * A {@link Cell} decorator that adds an icon to another {@link Cell}.
 *
 * <p>
 * Note: This class is new and its interface subject to change.
 * </p>
 *
 * @param <C> the type that this Cell represents
 */
public class IconCellDecorator<C> implements Cell<C> {

  private final Cell<C> cell;
  private final String iconHtml;
  private final int imageWidth;
  private final String outerDivHtml;
  private final String placeHolderHtml;

  /**
   * Construct a new {@link IconCellDecorator}. The icon and the content will be
   * middle aligned by default.
   *
   * @param icon the icon to use
   * @param cell the cell to decorate
   */
  public IconCellDecorator(ImageResource icon, Cell<C> cell) {
    this(icon, cell, HasVerticalAlignment.ALIGN_MIDDLE, 6);
  }

  /**
   * Construct a new {@link IconCellDecorator}.
   *
   * @param icon the icon to use
   * @param cell the cell to decorate
   * @param valign the vertical alignment attribute of the contents
   * @param spacing the pixel space between the icon and the cell
   */
  public IconCellDecorator(ImageResource icon, Cell<C> cell,
      VerticalAlignmentConstant valign, int spacing) {
    this.cell = cell;
    this.iconHtml = getImageHtml(icon, valign, false);
    this.imageWidth = icon.getWidth() + 6;
    this.placeHolderHtml = getImageHtml(icon, valign, true);

    // Cache the HTML for the outer div.
    String theOuterDivHtml = "<div style='position:relative;";
    if (LocaleInfo.getCurrentLocale().isRTL()) {
      theOuterDivHtml += "padding-right:";
    } else {
      theOuterDivHtml += "padding-left:";
    }
    theOuterDivHtml += imageWidth + "px;'>";
    this.outerDivHtml = theOuterDivHtml;
  }

  public boolean dependsOnSelection() {
    return cell.dependsOnSelection();
  }

  public Set<String> getConsumedEvents() {
    return cell.getConsumedEvents();
  }

  public boolean handlesSelection() {
    return cell.handlesSelection();
  }

  public boolean isEditing(Element element, C value, Object key) {
    return cell.isEditing(element, value, key);
  }

  public void onBrowserEvent(Element parent, C value, Object key,
      NativeEvent event, ValueUpdater<C> valueUpdater) {
    cell.onBrowserEvent(getCellParent(parent), value, key, event, valueUpdater);
  }

  public void render(C value, Object key, StringBuilder sb) {
    sb.append(outerDivHtml);
    if (isIconUsed(value)) {
      sb.append(getIconHtml(value));
    } else {
      sb.append(placeHolderHtml);
    }
    sb.append("<div>");
    cell.render(value, key, sb);
    sb.append("</div></div>");
  }

  public void setValue(Element parent, C value, Object key) {
    cell.setValue(getCellParent(parent), value, key);
  }

  /**
   * Get the HTML string that represents the icon. Override this method to
   * change the icon based on the value.
   *
   * @param value the value being rendered
   * @return the HTML string that represents the icon
   */
  protected String getIconHtml(C value) {
    return iconHtml;
  }

  /**
   * Check if the icon should be used for the value. If the icon should not be
   * used, a placeholder of the same size will be used instead. The default
   * implementations returns true.
   *
   * @param value the value being rendered
   * @return true to use the icon, false to use a placeholder
   */
  protected boolean isIconUsed(C value) {
    return true;
  }

  /**
   * Get the HTML representation of an image. Visible for testing.
   *
   * @param res the {@link ImageResource} to render as HTML
   * @param valign the vertical alignment
   * @param isPlaceholder if true, do not include the background image
   * @return the rendered HTML
   */
  // TODO(jlabanca): Move this to a Utility class.
  String getImageHtml(ImageResource res, VerticalAlignmentConstant valign,
      boolean isPlaceholder) {
    // Add the position and dimensions.
    StringBuilder sb = new StringBuilder();
    sb.append("<div style=\"position:absolute;top:0px;height:100%;");
    if (LocaleInfo.getCurrentLocale().isRTL()) {
      sb.append("right:0px;");
    } else {
      sb.append("left:0px;");
    }
    sb.append("width:").append(res.getWidth()).append("px;");

    // Add the background, vertically centered.
    if (!isPlaceholder) {
      String vert = valign == HasVerticalAlignment.ALIGN_MIDDLE
          ? "center" : valign.getVerticalAlignString();
      sb.append("background:url('").append(res.getURL()).append("') ");
      sb.append("no-repeat scroll ").append(vert).append(
          " center transparent;");
    }

    // Close the div and return.
    sb.append("\"></div>");
    return sb.toString();
  }

  /**
   * Get the parent element of the decorated cell.
   *
   * @param parent the parent of this cell
   * @return the decorated cell's parent
   */
  private Element getCellParent(Element parent) {
    return parent.getFirstChildElement().getChild(1).cast();
  }
}
