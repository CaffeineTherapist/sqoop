/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.sqoop.job.mr;

import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapreduce.Job;
import org.apache.sqoop.common.Direction;
import org.apache.sqoop.model.ConfigurationClass;
import org.apache.sqoop.model.Form;
import org.apache.sqoop.model.FormClass;
import org.apache.sqoop.model.Input;
import org.apache.sqoop.model.MJob;
import org.apache.sqoop.schema.Schema;
import org.apache.sqoop.schema.type.Text;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

/**
 * Current tests are using mockito to propagate credentials from Job object
 * to JobConf object. This implementation was chosen because it's not clear
 * how MapReduce is converting one object to another.
 */
public class TestConfigurationUtils {

  Job job;
  JobConf jobConf;

  @Before
  public void setUp() throws Exception {
    setUpJob();
    setUpJobConf();
  }

  public void setUpJob() throws Exception {
    job = new Job();
  }

  public void setUpJobConf() throws Exception {
    jobConf = spy(new JobConf(job.getConfiguration()));
    when(jobConf.getCredentials()).thenReturn(job.getCredentials());
  }

  @Test
  public void testConfigConnectorConnection() throws Exception {
    ConfigurationUtils.setConnectorConnectionConfig(Direction.FROM, job, getConfig());
    setUpJobConf();
    assertEquals(getConfig(), ConfigurationUtils.getConnectorConnectionConfig(Direction.FROM, jobConf));

    ConfigurationUtils.setConnectorConnectionConfig(Direction.TO, job, getConfig());
    setUpJobConf();
    assertEquals(getConfig(), ConfigurationUtils.getConnectorConnectionConfig(Direction.TO, jobConf));
  }

  @Test
  public void testConfigConnectorJob() throws Exception {
    ConfigurationUtils.setConnectorJobConfig(Direction.FROM, job, getConfig());
    setUpJobConf();
    assertEquals(getConfig(), ConfigurationUtils.getConnectorJobConfig(Direction.FROM, jobConf));

    ConfigurationUtils.setConnectorJobConfig(Direction.TO, job, getConfig());
    setUpJobConf();
    assertEquals(getConfig(), ConfigurationUtils.getConnectorJobConfig(Direction.TO, jobConf));
  }

  @Test
  public void testConfigFrameworkConnection() throws Exception {
    ConfigurationUtils.setFrameworkConnectionConfig(Direction.FROM, job, getConfig());
    setUpJobConf();
    assertEquals(getConfig(), ConfigurationUtils.getFrameworkConnectionConfig(Direction.FROM, jobConf));

    ConfigurationUtils.setFrameworkConnectionConfig(Direction.TO, job, getConfig());
    setUpJobConf();
    assertEquals(getConfig(), ConfigurationUtils.getFrameworkConnectionConfig(Direction.TO, jobConf));
  }

  @Test
  public void testConfigFrameworkJob() throws Exception {
    ConfigurationUtils.setFrameworkJobConfig(job, getConfig());
    setUpJobConf();
    assertEquals(getConfig(), ConfigurationUtils.getFrameworkJobConfig(jobConf));
  }

  @Test
  public void testConnectorSchema() throws Exception {
    ConfigurationUtils.setConnectorSchema(Direction.FROM, job, getSchema("a"));
    assertEquals(getSchema("a"), ConfigurationUtils.getConnectorSchema(Direction.FROM, jobConf));

    ConfigurationUtils.setConnectorSchema(Direction.TO, job, getSchema("b"));
    assertEquals(getSchema("b"), ConfigurationUtils.getConnectorSchema(Direction.TO, jobConf));
  }

  @Test
  public void testConnectorSchemaNull() throws Exception {
    ConfigurationUtils.setConnectorSchema(Direction.FROM, job, null);
    assertNull(ConfigurationUtils.getConnectorSchema(Direction.FROM, jobConf));

    ConfigurationUtils.setConnectorSchema(Direction.TO, job, null);
    assertNull(ConfigurationUtils.getConnectorSchema(Direction.FROM, jobConf));
  }

  private Schema getSchema(String name) {
    return new Schema(name).addColumn(new Text("c1"));
  }

  private Config getConfig() {
    Config c = new Config();
    c.f.A = "This is secret text!";
    return c;
  }

  @FormClass
  public static class F {

    @Input String A;

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof F)) return false;

      F f = (F) o;

      if (A != null ? !A.equals(f.A) : f.A != null) return false;

      return true;
    }

    @Override
    public int hashCode() {
      return A != null ? A.hashCode() : 0;
    }
  }

  @ConfigurationClass
  public static class Config {
    @Form F f;

    public Config() {
      f = new F();
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof Config)) return false;

      Config config = (Config) o;

      if (f != null ? !f.equals(config.f) : config.f != null)
        return false;

      return true;
    }

    @Override
    public int hashCode() {
      return f != null ? f.hashCode() : 0;
    }
  }
}