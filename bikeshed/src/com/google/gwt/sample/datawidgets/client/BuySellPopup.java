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
package com.google.gwt.sample.datawidgets.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.sample.datawidgets.shared.StockQuote;
import com.google.gwt.sample.datawidgets.shared.Transaction;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.TextBox;

/**
 * A popup used for purchasing stock.
 */
public class BuySellPopup extends DialogBox {

  private StockQuote quote;

  /**
   * The table used for layout.
   */
  private FlexTable layout = new FlexTable();

  /**
   * The box used to change the quantity.
   */
  private TextBox quantityBox = new TextBox();

  /**
   * The button used to buy or sell.
   */
  private Button opButton;

  /**
   * True if we are buying, false if hiding.
   */
  private boolean isBuying;

  /**
   * The last transaction.
   */
  private Transaction transaction;

  public BuySellPopup() {
    super(false, true);
    setGlassEnabled(true);
    setWidget(layout);

    layout.setHTML(0, 0, "<b>Ticker:</b>");
    layout.setHTML(1, 0, "<b>Name:</b>");
    layout.setHTML(2, 0, "<b>Price:</b>");
    layout.setHTML(3, 0, "<b>Quantity:</b>");
    layout.setWidget(3, 1, quantityBox);

    // Buy Button.
    opButton = new Button("", new ClickHandler() {
      public void onClick(ClickEvent event) {
        try {
          int quantity = Integer.parseInt(quantityBox.getText());
          transaction = new Transaction(isBuying, quote.getTicker(), quantity);
          hide();
        } catch (NumberFormatException e) {
          Window.alert("You must enter a valid quantity");
        }
      }
    });
    layout.setWidget(4, 0, opButton);

    // Cancel Button.
    Button cancelButton = new Button("Cancel", new ClickHandler() {
      public void onClick(ClickEvent event) {
        hide();
      }
    });
    layout.setWidget(4, 1, cancelButton);
  }

  /**
   * Get the last transaction.
   * 
   * @return the last transaction, or null if cancelled
   */
  public Transaction getTransaction() {
    return transaction;
  }

  /**
   * Set the current {@link StockQuote}.
   * 
   * @param quote the stock quote to buy
   * @param isBuying true if buying the stock
   */
  public void setStockQuote(StockQuote quote, boolean isBuying) {
    this.quote = quote;
    String op = isBuying ? "Buy" : "Sell";
    setText(op + " " + quote.getTicker() + " (" + quote.getName() + ")");
    layout.setHTML(0, 1, quote.getTicker());
    layout.setHTML(1, 1, quote.getName());
    layout.setHTML(2, 1, quote.getDisplayPrice());
    quantityBox.setText("0");
    opButton.setText(op);
    this.isBuying = isBuying;
    transaction = null;
  }

  @Override
  protected void onLoad() {
    super.onLoad();
    DeferredCommand.addCommand(new Command() {
      public void execute() {
        quantityBox.selectAll();
        quantityBox.setFocus(true);
      }
    });
  }
}
