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

package org.apache.druid.indexing.common.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import org.apache.druid.segment.loading.StorageLocationConfig;
import org.joda.time.Period;

import javax.annotation.Nullable;
import java.io.File;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

public class TaskConfig
{
  public static final List<String> DEFAULT_DEFAULT_HADOOP_COORDINATES = ImmutableList.of(
      "org.apache.hadoop:hadoop-client:2.8.5"
  );

  private static final Period DEFAULT_DIRECTORY_LOCK_TIMEOUT = new Period("PT10M");
  private static final Period DEFAULT_GRACEFUL_SHUTDOWN_TIMEOUT = new Period("PT5M");

  @JsonProperty
  private final String baseDir;

  @JsonProperty
  private final File baseTaskDir;

  @JsonProperty
  private final String hadoopWorkingPath;

  @JsonProperty
  private final int defaultRowFlushBoundary;

  @JsonProperty
  private final List<String> defaultHadoopCoordinates;

  @JsonProperty
  private final boolean restoreTasksOnRestart;

  @JsonProperty
  private final Period gracefulShutdownTimeout;

  @JsonProperty
  private final Period directoryLockTimeout;

  @JsonProperty
  private final List<StorageLocationConfig> shuffleDataLocations;

  @JsonProperty
  private final boolean ignoreTimestampSpecForDruidInputSource;

  @JsonCreator
  public TaskConfig(
      @JsonProperty("baseDir") String baseDir,
      @JsonProperty("baseTaskDir") String baseTaskDir,
      @JsonProperty("hadoopWorkingPath") String hadoopWorkingPath,
      @JsonProperty("defaultRowFlushBoundary") Integer defaultRowFlushBoundary,
      @JsonProperty("defaultHadoopCoordinates") List<String> defaultHadoopCoordinates,
      @JsonProperty("restoreTasksOnRestart") boolean restoreTasksOnRestart,
      @JsonProperty("gracefulShutdownTimeout") Period gracefulShutdownTimeout,
      @JsonProperty("directoryLockTimeout") Period directoryLockTimeout,
      @JsonProperty("shuffleDataLocations") List<StorageLocationConfig> shuffleDataLocations,
      @JsonProperty("ignoreTimestampSpecForDruidInputSource") boolean ignoreTimestampSpecForDruidInputSource
  )
  {
    this.baseDir = baseDir == null ? System.getProperty("java.io.tmpdir") : baseDir;
    this.baseTaskDir = new File(defaultDir(baseTaskDir, "persistent/task"));
    // This is usually on HDFS or similar, so we can't use java.io.tmpdir
    this.hadoopWorkingPath = hadoopWorkingPath == null ? "/tmp/druid-indexing" : hadoopWorkingPath;
    this.defaultRowFlushBoundary = defaultRowFlushBoundary == null ? 75000 : defaultRowFlushBoundary;
    this.defaultHadoopCoordinates = defaultHadoopCoordinates == null
                                    ? DEFAULT_DEFAULT_HADOOP_COORDINATES
                                    : defaultHadoopCoordinates;
    this.restoreTasksOnRestart = restoreTasksOnRestart;
    this.gracefulShutdownTimeout = gracefulShutdownTimeout == null
                                   ? DEFAULT_GRACEFUL_SHUTDOWN_TIMEOUT
                                   : gracefulShutdownTimeout;
    this.directoryLockTimeout = directoryLockTimeout == null
                                ? DEFAULT_DIRECTORY_LOCK_TIMEOUT
                                : directoryLockTimeout;
    if (shuffleDataLocations == null) {
      this.shuffleDataLocations = Collections.singletonList(
          new StorageLocationConfig(new File(defaultDir(null, "intermediary-segments")), null, null)
      );
    } else {
      this.shuffleDataLocations = shuffleDataLocations;
    }
    this.ignoreTimestampSpecForDruidInputSource = ignoreTimestampSpecForDruidInputSource;
  }

  @JsonProperty
  public String getBaseDir()
  {
    return baseDir;
  }

  @JsonProperty
  public File getBaseTaskDir()
  {
    return baseTaskDir;
  }

  public File getTaskDir(String taskId)
  {
    return new File(baseTaskDir, taskId);
  }

  public File getTaskWorkDir(String taskId)
  {
    return new File(getTaskDir(taskId), "work");
  }

  public File getTaskTempDir(String taskId)
  {
    return new File(getTaskDir(taskId), "temp");
  }

  public File getTaskLockFile(String taskId)
  {
    return new File(getTaskDir(taskId), "lock");
  }

  @JsonProperty
  public String getHadoopWorkingPath()
  {
    return hadoopWorkingPath;
  }

  @JsonProperty
  public int getDefaultRowFlushBoundary()
  {
    return defaultRowFlushBoundary;
  }

  @JsonProperty
  public List<String> getDefaultHadoopCoordinates()
  {
    return defaultHadoopCoordinates;
  }

  @JsonProperty
  public boolean isRestoreTasksOnRestart()
  {
    return restoreTasksOnRestart;
  }

  @JsonProperty
  public Period getGracefulShutdownTimeout()
  {
    return gracefulShutdownTimeout;
  }

  @JsonProperty
  public Period getDirectoryLockTimeout()
  {
    return directoryLockTimeout;
  }

  @JsonProperty
  public List<StorageLocationConfig> getShuffleDataLocations()
  {
    return shuffleDataLocations;
  }

  @JsonProperty
  public boolean isIgnoreTimestampSpecForDruidInputSource()
  {
    return ignoreTimestampSpecForDruidInputSource;
  }

  private String defaultDir(@Nullable String configParameter, final String defaultVal)
  {
    if (configParameter == null) {
      return Paths.get(getBaseDir(), defaultVal).toString();
    }

    return configParameter;
  }
}
