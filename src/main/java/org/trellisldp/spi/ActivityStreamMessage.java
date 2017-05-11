/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.trellisldp.spi;

import static java.util.stream.Collectors.toList;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import org.apache.commons.rdf.api.IRI;

/**
 * A structure used for serializing an Event into an ActivityStream 2.0 JSON object
 *
 * @see <a href="https://www.w3.org/TR/activitystreams-core/">Activity Streams 2.0</a>
 *
 * @author acoburn
 */
class ActivityStreamMessage {

    /**
     * The target resource of a message
     */
    static class EventResource {
        /**
         * The resource identifier
         */
        public String id;

        /**
         * The resource types
         */
        public List<String> type;

        /**
         * Create a new event resource target
         * @param id the identifier
         * @param type the types
         */
        public EventResource(final String id, final List<String> type) {
            this.id = id;
            this.type = type;
        }
    }

    /**
     * The event identifier
     */
    public String id;

    /**
     * The event types
     */
    public List<String> type;

    /**
     * The inbox assocated with the resource
     */
    public String inbox;

    /**
     * The actors associated with this event
     */
    public List<String> actor;

    /**
     * The target resource
     */
    public EventResource object;

    /**
     * The JSON-LD context
     */
    @JsonProperty("@context")
    public String context = "https://www.w3.org/ns/activitystreams";

    /**
     * Populate a ActivityStreamMessage from an Event
     * @param event The event
     * @return an ActivityStreamMessage
     */
    public static ActivityStreamMessage from(final Event event) {

        final ActivityStreamMessage msg = new ActivityStreamMessage();

        msg.id = event.getIdentifier().getIRIString();
        msg.type = event.getTypes().stream().map(IRI::getIRIString).collect(toList());
        msg.actor = event.getAgents().stream().map(IRI::getIRIString).collect(toList());

        event.getInbox().map(IRI::getIRIString).ifPresent(inbox -> msg.inbox = inbox);
        event.getTarget().map(IRI::getIRIString).ifPresent(target ->
            msg.object = new EventResource(target,
                    event.getTargetTypes().stream().map(IRI::getIRIString).collect(toList())));

        return msg;
    }
}