/*
 * Copyright 2000-2013 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jetbrains.buildServer.gradle.server;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jetbrains.buildServer.gradle.GradleRunnerConstants;
import jetbrains.buildServer.requirements.Requirement;
import jetbrains.buildServer.requirements.RequirementType;
import jetbrains.buildServer.serverSide.PropertiesProcessor;
import jetbrains.buildServer.serverSide.RunType;
import jetbrains.buildServer.serverSide.RunTypeRegistry;
import jetbrains.buildServer.util.StringUtil;
import org.jetbrains.annotations.NotNull;

public class GradleRunType extends RunType {

  public GradleRunType(final RunTypeRegistry runTypeRegistry) {
    runTypeRegistry.registerRunType(this);
  }

  @NotNull
  @Override
  public String getType() {
    return GradleRunnerConstants.RUNNER_TYPE;
  }

  @NotNull
  @Override
  public String getDisplayName() {
    return "Gradle";
  }

  @NotNull
  @Override
  public String getDescription() {
    return "Runner for Gradle projects";
  }

  @Override
  public PropertiesProcessor getRunnerPropertiesProcessor() {
    // verify user settings and indicate which of them are invalid
    return null;
  }

  @Override
  public String getEditRunnerParamsJspFilePath() {
    return "editGradleRunnerRunParams.jsp";
  }

  @Override
  public String getViewRunnerParamsJspFilePath() {
    return "viewGradleRunnerRunParams.jsp";
  }

  @Override
  public Map<String, String> getDefaultRunnerProperties() {
    final Map<String, String> map = new HashMap<String, String>();
    map.put(GradleRunnerConstants.PATH_TO_BUILD_FILE, "build.gradle");
    map.put(GradleRunnerConstants.GRADLE_WRAPPER_FLAG, "true");
    return map;
  }

  @NotNull
  @Override
  public String describeParameters(@NotNull final Map<String, String> parameters) {

    StringBuilder result = new StringBuilder();

    if (Boolean.valueOf(parameters.get(GradleRunnerConstants.IS_INCREMENTAL))) {
      result.append("Run incremental builds using :buildDependents");
    } else {
      result.append("Gradle tasks: ");
      String gradleTasks = parameters.get(GradleRunnerConstants.GRADLE_TASKS);
      if (StringUtil.isEmpty(gradleTasks)) {
        result.append("Default");
      } else {
        result.append(gradleTasks);
      }
    }
    result.append('\n');
    result.append("Use wrapper script: ");
    if (Boolean.valueOf(parameters.get(GradleRunnerConstants.GRADLE_WRAPPER_FLAG))) {
      result.append("yes");
    } else {
      result.append("no");
    }

    return result.toString();
  }

  @NotNull
  @Override
  public List<Requirement> getRunnerSpecificRequirements(@NotNull final Map<String, String> runParameters) {
    if(!Boolean.valueOf(runParameters.get(GradleRunnerConstants.GRADLE_WRAPPER_FLAG))
       && StringUtil.isEmptyOrSpaces(runParameters.get(GradleRunnerConstants.GRADLE_HOME))) {
      return Collections.singletonList(new Requirement("env.GRADLE_HOME", null, RequirementType.EXISTS));
    } else {
      return Collections.emptyList();
    }
  }
}
