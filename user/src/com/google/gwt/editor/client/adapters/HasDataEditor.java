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
package com.google.gwt.editor.client.adapters;

import com.google.gwt.editor.client.LeafValueEditor;
import com.google.gwt.view.client.HasData;

import java.util.Collections;

/**
 * Adapts the HasData interface to the Editor framework.
 * 
 * @param <T> the type of data to be edited
 */
public class HasDataEditor<T> extends ListEditor<T, LeafValueEditor<T>> {
  static class HasDataEditorSource<T> extends EditorSource<LeafValueEditor<T>> {
    private final HasData<T> data;

    public HasDataEditorSource(HasData<T> data) {
      this.data = data;
    }

    @Override
    public IndexedEditor<T> create(int index) {
      assert index >= 0;
      return new IndexedEditor<T>(index, data);
    }

    @Override
    public LeafValueEditor<T> createEditorForTraversal() {
      return new IndexedEditor<T>(-1, null);
    }

    @Override
    public void dispose(LeafValueEditor<T> subEditor) {
      // We use a negative index as flag in createEditorForTraversal
      assert ((IndexedEditor<T>) subEditor).index >= 0;
      data.setRowCount(data.getRowCount() - 1);
    }

    @Override
    public void setIndex(LeafValueEditor<T> editor, int index) {
      assert index >= 0;
      ((IndexedEditor<T>) editor).setIndex(index);
    }
  }

  private static class IndexedEditor<Q> implements LeafValueEditor<Q> {
    private int index;
    private Q value;
    private final HasData<Q> data;

    IndexedEditor(int index, HasData<Q> data) {
      this.index = index;
      this.data = data;
    }

    @Override
    public Q getValue() {
      return value;
    }

    @Override
    public void setValue(Q value) {
      this.value = value;
      push();
    }

    void setIndex(int index) {
      assert index >= 0;
      this.index = index;
      push();
    }

    private void push() {
      if (data != null) {
        data.setRowData(index, Collections.singletonList(value));
      }
    }
  }

  /**
   * Create a HasDataEditor backed by a HasData.
   * 
   * @param <T> the type of data to be edited
   * @param data the HasData that is displaying the data
   * @return a instance of a HasDataEditor
   */
  public static <T> HasDataEditor<T> of(HasData<T> data) {
    return new HasDataEditor<T>(data);
  }

  /**
   * Prevent subclassing.
   */
  private HasDataEditor(HasData<T> data) {
    super(new HasDataEditorSource<T>(data));
  }
}
