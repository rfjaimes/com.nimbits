/*
 * Copyright (c) 2010 Tonic Solutions LLC.
 *
 * http://www.nimbits.com
 *
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, eitherexpress or implied. See the License for the specific language governing permissions and limitations under the License.
 */

/**
 * @fileoverview A wrapper around the Dygraph class which implements the
 * interface for a GViz (aka Google Visualization API) visualization.
 * It is designed to be a drop-in replacement for Google's AnnotatedTimeline,
 * so the documentation at
 * http://code.google.com/apis/chart/interactive/docs/gallery/annotatedtimeline.html
 * translates over directly.
 *
 * For a full demo, see:
 * - http://dygraphs.com/tests/gviz.html
 * - http://dygraphs.com/tests/annotation-gviz.html
 */

/**
 * A wrapper around Dygraph that implements the gviz API.
 * @param {Object} container The DOM object the visualization should live in.
 */
Dygraph.GVizChart = function(container) {
  this.container = container;
}

Dygraph.GVizChart.prototype.draw = function(data, options) {
  // Clear out any existing dygraph.
  // TODO(danvk): would it make more sense to simply redraw using the current
  // date_graph object?
  this.container.innerHTML = '';
  if (typeof(this.date_graph) != 'undefined') {
    this.date_graph.destroy();
  }

  this.date_graph = new Dygraph(this.container, data, options);
}

/**
 * Google charts compatible setSelection
 * Only row selection is supported, all points in the row will be highlighted
 * @param {Array} array of the selected cells
 * @public
 */
Dygraph.GVizChart.prototype.setSelection = function(selection_array) {
  var row = false;
  if (selection_array.length) {
    row = selection_array[0].row;
  }
  this.date_graph.setSelection(row);
}

/**
 * Google charts compatible getSelection implementation
 * @return {Array} array of the selected cells
 * @public
 */
Dygraph.GVizChart.prototype.getSelection = function() {
  var selection = [];

  var row = this.date_graph.getSelection();

  if (row < 0) return selection;

  col = 1;
  for (var i in this.date_graph.layout_.datasets) {
    selection.push({row: row, column: col});
    col++;
  }

  return selection;
}

