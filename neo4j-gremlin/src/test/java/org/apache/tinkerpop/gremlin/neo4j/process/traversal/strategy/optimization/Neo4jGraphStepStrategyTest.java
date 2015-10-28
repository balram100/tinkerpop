package org.apache.tinkerpop.gremlin.neo4j.process.traversal.strategy.optimization;

import org.apache.tinkerpop.gremlin.LoadGraphWith;
import org.apache.tinkerpop.gremlin.neo4j.AbstractNeo4jGremlinTest;
import org.apache.tinkerpop.gremlin.neo4j.process.traversal.step.sideEffect.Neo4jGraphStep;
import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.step.filter.HasStep;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Daniel Kuppitz (http://gremlin.guru)
 */
public class Neo4jGraphStepStrategyTest extends AbstractNeo4jGremlinTest {

    @Test
    @LoadGraphWith(LoadGraphWith.GraphData.MODERN)
    public void shouldFoldInHasContainers() {
        GraphTraversal.Admin traversal = g.V().has("name", "marko").asAdmin();
        assertEquals(2, traversal.getSteps().size());
        assertEquals(HasStep.class, traversal.getEndStep().getClass());
        traversal.applyStrategies();
        assertEquals(1, traversal.getSteps().size());
        assertEquals(Neo4jGraphStep.class, traversal.getStartStep().getClass());
        assertEquals(Neo4jGraphStep.class, traversal.getEndStep().getClass());
        assertEquals(1, ((Neo4jGraphStep) traversal.getStartStep()).getHasContainers().size());
        assertEquals("name", ((Neo4jGraphStep<?, ?>) traversal.getStartStep()).getHasContainers().get(0).getKey());
        assertEquals("marko", ((Neo4jGraphStep<?, ?>) traversal.getStartStep()).getHasContainers().get(0).getValue());
        ////
        traversal = g.V().has("name", "marko").has("age", P.gt(20)).asAdmin();
        traversal.applyStrategies();
        assertEquals(1, traversal.getSteps().size());
        assertEquals(Neo4jGraphStep.class, traversal.getStartStep().getClass());
        assertEquals(2, ((Neo4jGraphStep) traversal.getStartStep()).getHasContainers().size());
        ////
        traversal = g.V().has("name", "marko").out().has("name", "daniel").asAdmin();
        traversal.applyStrategies();
        assertEquals(3, traversal.getSteps().size());
        assertEquals(Neo4jGraphStep.class, traversal.getStartStep().getClass());
        assertEquals(1, ((Neo4jGraphStep) traversal.getStartStep()).getHasContainers().size());
        assertEquals("name", ((Neo4jGraphStep<?, ?>) traversal.getStartStep()).getHasContainers().get(0).getKey());
        assertEquals("marko", ((Neo4jGraphStep<?, ?>) traversal.getStartStep()).getHasContainers().get(0).getValue());
        assertEquals(HasStep.class, traversal.getEndStep().getClass());
    }
}
