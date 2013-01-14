/*
 * Copyright 2012 the original author or authors.
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

package org.gradle.docs.releasenotes

class StaticReleaseNotesTest extends AbstractReleaseNotesTest {

    def "has fixed issues holder"() {
        expect:
        !renderedDocument.body().select("h2#fixed-issues").empty
    }

    def "line length check"() {
        when:
        def maxLineLength = 220
        def tooLongLines = [:] // key is line num, value is line
        sourceText.eachLine { String line, int num ->
            if (line.size() > maxLineLength) {
                tooLongLines[num] = line
            }
        }

        then:
        tooLongLines.isEmpty()
    }

    def "no duplicate ids"() {
        when:
        def groupedElements = renderedDocument.body().allElements.findAll { it.id() }.groupBy { it.id() }
        def duplicateIds = groupedElements.keySet().findAll { groupedElements[it].size() > 1 }

        then:
        duplicateIds.empty
    }

    def "no broken internal links"() {
        when:
        def brokenAnchorLinks = []
        def links = renderedDocument.select("a")
        def ids = renderedDocument.allElements.findAll { it.id() }*.id()
        def anchors = links.findAll { it.attr("name") }*.attr("name")

        links.each {
            def href = it.attr("href")
            if (href.startsWith("#")) {
                def target = href[1..-1]
                if (!ids.contains(target) && !anchors.contains(target)) {
                    brokenAnchorLinks << target
                }
            }
        }

        then:
        brokenAnchorLinks.empty
    }
}