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
package org.apache.nemo.compiler.optimizer.pass.compiletime.reshaping;

import org.apache.nemo.common.ir.IRDAG;
import org.apache.nemo.common.ir.edge.executionproperty.CommunicationPatternProperty;
import org.apache.nemo.common.ir.vertex.system.StreamVertex;
import org.apache.nemo.compiler.optimizer.pass.compiletime.Requires;

/**
 * Inserts the StreamVertex for each shuffle edge.
 */
@Requires(CommunicationPatternProperty.class)
public final class LargeShuffleRelayReshapingPass extends ReshapingPass {

  /**
   * Default constructor.
   */
  public LargeShuffleRelayReshapingPass() {
    super(LargeShuffleRelayReshapingPass.class);
  }


  @Override
  public void optimize(final IRDAG dag) {
    dag.topologicalDo(vertex -> {
      dag.getIncomingEdgesOf(vertex).forEach(edge -> {
        if (CommunicationPatternProperty.Value.Shuffle
          .equals(edge.getPropertyValue(CommunicationPatternProperty.class).get())) {
          dag.insert(new StreamVertex(), edge);
        }
      });
    });
  }
}
