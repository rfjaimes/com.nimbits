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

import com.nimbits.client.NimbitsClient;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.category.Category;
import com.nimbits.client.model.category.CategoryName;
import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointName;
import com.nimbits.client.model.value.Value;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.junit.Assert.assertNotNull;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 5/9/11
 * Time: 8:25 PM
 */
public class LocalTest {

    NimbitsClient c = ClientHelper.client();

    @Before
    @Ignore
    public void setup() throws NimbitsException {
        List<Category> categoryList = c.getCategories(true, true);
        for (Category x : categoryList) {
            c.deleteCategory(x.getName());
        }
    }


    @Test
    public void loadRandomTest() throws IOException {

        CategoryName cName = CommonFactoryLocator.getInstance().createCategoryName(UUID.randomUUID().toString());
        Category cx = c.addCategory(cName);

        PointName pointName = CommonFactoryLocator.getInstance().createPointName(UUID.randomUUID().toString());
        Point p = c.addPoint(cName, pointName);
        assertNotNull(p);
        long now = new Date().getTime();
        long then = now - 1000 * 100;
        Random r = new Random();
        double d = 0;

        while (then < now) {
            then += 1000;
            d += 1;
            assertNotNull(c.recordValue(pointName, r.nextDouble() * 100, new Date(then)).getNumberValue());
        }


    }

    @Test
    public void loadLineTest() throws IOException {
        long now = new Date().getTime();
        long then = now - 1000 * 100;
        double d = 0;

        PointName pointName = CommonFactoryLocator.getInstance().createPointName(UUID.randomUUID().toString());
        CategoryName categoryName = CommonFactoryLocator.getInstance().createCategoryName(null);
        c.addPoint(categoryName, pointName);

        while (then < now) {
            then += 1000;
            d += 1;

            Value v = c.recordValue(pointName, d, new Date(then));
            assertNotNull(v);
        }


    }
}