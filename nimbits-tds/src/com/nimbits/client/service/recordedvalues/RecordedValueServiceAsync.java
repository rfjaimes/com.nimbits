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
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.client.service.recordedvalues;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.exceptions.CalculationFailedException;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointName;
import com.nimbits.client.model.timespan.Timespan;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;

import java.util.Date;
import java.util.List;

public interface RecordedValueServiceAsync {


    void getDataSeries(final List<Point> arrayList, final Timespan timespan, final AsyncCallback<List<Point>> asyncCallback);

    void getTopDataSeries(final Point point, final int maxValues,
                          final AsyncCallback<Point> asyncCallback);

    void getCurrentValue(final Point p, final AsyncCallback<Value> asyncCallback) throws NimbitsException;


    void solveEquation(final Point point, final AsyncCallback<Double> asyncCallback) throws CalculationFailedException, NimbitsException;

    void getPieceOfDataSegment(final Point point, final Timespan timespan, final int start, final int end, final AsyncCallback<List<Value>> asyncCallback);

    // void getCurrentValue(final User u, final Point p, final AsyncCallback<Value> callback) throws NimbitsException;

    void getPrevValue(final Point p, final Date date, final AsyncCallback<Value> callback);

    // void recordValue(final User u, final Point target, final Value value, final AsyncCallback<Value> callback) throws NimbitsException;

    void recordValue(final User u, final PointName pointName, final Value value, final AsyncCallback<Value> callback) throws NimbitsException;

    void recordValue(final Point point, final Value value, final AsyncCallback<Value> asyncCallback) throws NimbitsException;

    void getLastRecordedDate(final List<Point> points, final AsyncCallback<Date> callback);

    void getTopDataSeries(final Point point, int maxValues, final Date endDate, final AsyncCallback<List<Value>> async);

    void getCurrentValue(long pointOwnerId, final PointName pointName, final AsyncCallback<Value> async) throws NimbitsException;


    void recordValue(final User u, final Point target, final Value value, final boolean loopFlag, AsyncCallback<Value> async);

    void getDataSegment(final Point point,
                        final Timespan timespan,
                        final int start,
                        final int end, AsyncCallback<List<Value>> async);


    void getDataSegment(final Point point,
                        final Timespan timespan, AsyncCallback<List<Value>> async);


    void getCache(final Point point, AsyncCallback<List<Value>> async);
}