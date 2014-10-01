package com.flightstats.analytics.tree.decision;

import com.flightstats.analytics.tree.LabeledMixedItem;
import com.flightstats.analytics.tree.MixedItem;
import com.flightstats.analytics.tree.Splitter;
import com.flightstats.analytics.tree.Tree;
import lombok.AllArgsConstructor;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.flightstats.analytics.tree.decision.DecisionTreeTrainerTest.Humidity.HIGH;
import static com.flightstats.analytics.tree.decision.DecisionTreeTrainerTest.Humidity.NORMAL;
import static com.flightstats.analytics.tree.decision.DecisionTreeTrainerTest.Outlook.*;
import static com.flightstats.analytics.tree.decision.DecisionTreeTrainerTest.Temp.*;
import static com.flightstats.analytics.tree.decision.DecisionTreeTrainerTest.Wind.STRONG;
import static com.flightstats.analytics.tree.decision.DecisionTreeTrainerTest.Wind.WEAK;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class DecisionTreeTrainerTest {
    @Test
    public void testImpurity_allSame() throws Exception {
        DecisionTreeTrainer testClass = new DecisionTreeTrainer(mock(Splitter.class));
        LabeledMixedItem<Integer> item1 = new LabeledMixedItem<>(null, 1);
        LabeledMixedItem<Integer> item2 = new LabeledMixedItem<>(null, 1);
        LabeledMixedItem<Integer> item3 = new LabeledMixedItem<>(null, 1);
        double impurity = testClass.impurity(Arrays.asList(item1, item2, item3));
        assertEquals(0.0, impurity, 0.0001);
    }

    @Test
    public void testImpurity_allDifferent() throws Exception {
        DecisionTreeTrainer testClass = new DecisionTreeTrainer(mock(Splitter.class));
        LabeledMixedItem<Integer> item1 = new LabeledMixedItem<>(null, 1);
        LabeledMixedItem<Integer> item2 = new LabeledMixedItem<>(null, 2);
        double impurity = testClass.impurity(Arrays.asList(item1, item2));
        assertEquals(0.25, impurity, 0.0001);
    }

    @Test
    public void testSimpleExample() throws Exception {
        LabeledMixedItem<Integer> one = new LabeledMixedItem<>(new MixedItem("1", fruitData(true, true, false), new HashMap<>()), 1);
        LabeledMixedItem<Integer> two = new LabeledMixedItem<>(new MixedItem("2", fruitData(true, true, false), new HashMap<>()), 1);

        LabeledMixedItem<Integer> three = new LabeledMixedItem<>(new MixedItem("3", fruitData(false, true, true), new HashMap<>()), 0);
        LabeledMixedItem<Integer> four = new LabeledMixedItem<>(new MixedItem("4", fruitData(false, false, true), new HashMap<>()), 0);
        LabeledMixedItem<Integer> five = new LabeledMixedItem<>(new MixedItem("5", fruitData(true, false, true), new HashMap<>()), 0);

        LabeledMixedItem<Integer> six = new LabeledMixedItem<>(new MixedItem("6", fruitData(true, true, true), new HashMap<>()), 1);

        //we put 'one' in the list twice, to unbalance the weights and make bitter the best entropy gain. (otherwise, the resulting tree isn't deterministic)
        List<LabeledMixedItem<Integer>> LabeledMixedItems = Arrays.asList(one, one, two, three, four, five, six);

        Tree<Integer> tree = new DecisionTreeTrainer(new Splitter<>()).train("fruits", LabeledMixedItems, Arrays.asList("sweet", "sour", "bitter"), 3, 0);

        assertEquals((Integer) 1, tree.evaluate(one.getItem()));
        assertEquals((Integer) 1, tree.evaluate(two.getItem()));
        assertEquals((Integer) 0, tree.evaluate(three.getItem()));
        assertEquals((Integer) 0, tree.evaluate(four.getItem()));
        assertEquals((Integer) 0, tree.evaluate(five.getItem()));
        assertEquals((Integer) 1, tree.evaluate(six.getItem()));

        //we like these, because they aren't bitter (which is the strongest signal in the tree above).
        assertEquals((Integer) 1, tree.evaluate(new MixedItem("bland", fruitData(false, false, false), new HashMap<>())));
        assertEquals((Integer) 1, tree.evaluate(new MixedItem("sour", fruitData(false, true, false), new HashMap<>())));
    }

    private Map<String, Integer> fruitData(boolean sweet, boolean sour, boolean bitter) {
        Map<String, Integer> data = new HashMap<>();
        data.put("sour", sour ? 1 : 0);
        data.put("sweet", sweet ? 1 : 0);
        data.put("bitter", bitter ? 1 : 0);
        return data;
    }

    @Test
    public void testTennisExample() throws Exception {
        List<LabeledMixedItem<Integer>> trainingData = Arrays.asList(
                new LabeledMixedItem<>(new MixedItem("1", tennisData(SUNNY, HOT, HIGH, WEAK), new HashMap<>()), 0),
                new LabeledMixedItem<>(new MixedItem("1", tennisData(SUNNY, HOT, HIGH, STRONG), new HashMap<>()), 0),
                new LabeledMixedItem<>(new MixedItem("1", tennisData(OVERCAST, HOT, HIGH, WEAK), new HashMap<>()), 1),
                new LabeledMixedItem<>(new MixedItem("1", tennisData(RAIN, MILD, HIGH, WEAK), new HashMap<>()), 1),

                new LabeledMixedItem<>(new MixedItem("1", tennisData(RAIN, COOL, NORMAL, WEAK), new HashMap<>()), 1),
                new LabeledMixedItem<>(new MixedItem("1", tennisData(RAIN, COOL, NORMAL, STRONG), new HashMap<>()), 0),
                new LabeledMixedItem<>(new MixedItem("1", tennisData(OVERCAST, COOL, NORMAL, STRONG), new HashMap<>()), 1),
                new LabeledMixedItem<>(new MixedItem("1", tennisData(SUNNY, MILD, HIGH, WEAK), new HashMap<>()), 0),

                new LabeledMixedItem<>(new MixedItem("1", tennisData(SUNNY, COOL, NORMAL, WEAK), new HashMap<>()), 1),
                new LabeledMixedItem<>(new MixedItem("1", tennisData(RAIN, MILD, NORMAL, WEAK), new HashMap<>()), 1),
                new LabeledMixedItem<>(new MixedItem("1", tennisData(SUNNY, MILD, NORMAL, STRONG), new HashMap<>()), 1),
                new LabeledMixedItem<>(new MixedItem("1", tennisData(OVERCAST, MILD, HIGH, STRONG), new HashMap<>()), 1),

                new LabeledMixedItem<>(new MixedItem("1", tennisData(OVERCAST, HOT, NORMAL, WEAK), new HashMap<>()), 1),
                new LabeledMixedItem<>(new MixedItem("1", tennisData(RAIN, MILD, HIGH, STRONG), new HashMap<>()), 0)
        );

        com.flightstats.analytics.tree.decision.DecisionTreeTrainer testClass = new com.flightstats.analytics.tree.decision.DecisionTreeTrainer(new Splitter<>());
        List<String> attributes = Arrays.asList("outlook", "temp", "humidity", "wind");

        Tree<Integer> tennis = testClass.train("tennis", trainingData, attributes, 4, 0);

        assertEquals((Integer) 0, tennis.evaluate(new MixedItem("2", tennisData(RAIN, HOT, HIGH, STRONG), new HashMap<>())));
        //todo: figure out some assertions to make here?
    }

    private Map<String, Integer> tennisData(Outlook outlook, Temp temperature, Humidity humidity, Wind wind) {
        Map<String, Integer> data = new HashMap<>();
        data.put("outlook", outlook.value);
        data.put("temp", temperature.value);
        data.put("humidity", humidity.value);
        data.put("wind", wind.value);
        return data;
    }

    @AllArgsConstructor
    static enum Outlook {
        SUNNY(1), OVERCAST(2), RAIN(3);
        int value;
    }

    @AllArgsConstructor
    static enum Temp {
        HOT(1), MILD(2), COOL(3);
        int value;
    }

    @AllArgsConstructor
    static enum Humidity {
        HIGH(1), NORMAL(2);
        int value;
    }

    @AllArgsConstructor
    static enum Wind {
        WEAK(1), STRONG(2);
        int value;
    }


}