/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.nemo.compiler.optimizer.pass.compiletime.composite;

import org.apache.nemo.client.JobLauncher;
import org.apache.nemo.common.dag.DAG;
import org.apache.nemo.common.ir.edge.IREdge;
import org.apache.nemo.common.ir.edge.executionproperty.DataFlowProperty;
import org.apache.nemo.common.ir.edge.executionproperty.DataStoreProperty;
import org.apache.nemo.common.ir.vertex.IRVertex;
import org.apache.nemo.common.ir.vertex.executionproperty.ResourcePriorityProperty;
import org.apache.nemo.compiler.CompilerTestUtil;
import org.apache.nemo.compiler.optimizer.pass.compiletime.annotating.TransientResourceDataStorePass;
import org.apache.nemo.compiler.optimizer.pass.compiletime.annotating.TransientResourcePriorityPass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;

/**
 * Test {@link TransientResourcePriorityPass} and {@link TransientResourceDataStorePass}.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(JobLauncher.class)
public class TransientResourceCompositePassTest {
  private DAG<IRVertex, IREdge> compiledDAG;

  @Before
  public void setUp() throws Exception {
    compiledDAG = CompilerTestUtil.compileALSDAG();
  }

  @Test
  public void testTransientResourcePass() throws Exception {
    final DAG<IRVertex, IREdge> processedDAG = new TransientResourceCompositePass().apply(compiledDAG);

    final IRVertex vertexX = processedDAG.getTopologicalSort().get(0);
    assertEquals(ResourcePriorityProperty.TRANSIENT, vertexX.getPropertyValue(ResourcePriorityProperty.class).get());

    final IRVertex vertexY = processedDAG.getTopologicalSort().get(5);
    assertEquals(ResourcePriorityProperty.TRANSIENT, vertexY.getPropertyValue(ResourcePriorityProperty.class).get());
    processedDAG.getIncomingEdgesOf(vertexY).forEach(irEdge -> {
      assertEquals(DataStoreProperty.Value.MemoryStore, irEdge.getPropertyValue(DataStoreProperty.class).get());
      assertEquals(DataFlowProperty.Value.Pull, irEdge.getPropertyValue(DataFlowProperty.class).get());
    });
  }
}
