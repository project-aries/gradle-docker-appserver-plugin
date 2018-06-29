/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.aries.gradle.docker.appservers.plugin

import org.gradle.testkit.runner.BuildResult
import spock.lang.Timeout

import static java.util.concurrent.TimeUnit.MINUTES

/**
 *
 *  Functional tests to perform up, pause, and down tasks on dockerized application server.
 *
 */
class WildflyFunctionalTest extends AbstractFunctionalTest {

    @Timeout(value = 5, unit = MINUTES)
    def "Can start, stop, and remove a wildfly application stack"() {

        String uuid = randomString()
        buildFile << """
            
            appservers {
                randomPorts()
            }
            applications {
                wildfly {
                    id = "${uuid}"
                }
            }
            task up(dependsOn: ['wildflyUp']) {
                doLast {
                    logger.quiet 'FOUND INSPECTION: ' + wildflyUp.ext.inspection
                }
            }
            
            task stop(dependsOn: ['wildflyStop'])

            task down(dependsOn: ['wildflyDown'])
        """

        when:
            BuildResult result = build('up')

        then:
            result.output.contains('is not running or available to inspect')
            result.output.contains('Inspecting container with ID')
            result.output.contains('Created container with ID')
            result.output.contains('Starting liveness probe on container')
            result.output.contains('Running exec-stop on container with ID')
            result.output.contains('Removing container with ID')
            result.output.contains('RestartContainer SKIPPED')
            result.output.contains('->9990')
            !result.output.contains('9990->9990')
            !result.output.contains('ListImages SKIPPED')
    }
}
