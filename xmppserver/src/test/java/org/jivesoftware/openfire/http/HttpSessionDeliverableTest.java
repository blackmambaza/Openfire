/*
 * Copyright (C) 2023 Ignite Realtime Foundation. All rights reserved.
 *
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
package org.jivesoftware.openfire.http;

import org.dom4j.QName;
import org.junit.jupiter.api.Test;
import org.xmpp.packet.Message;
import org.xmpp.packet.Packet;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for {@link HttpSession.Deliverable}
 *
 * @author Guus der Kinderen, guus.der.kinderen@gmail.com
 */
public class HttpSessionDeliverableTest
{
    /**
     * Verifies that the default namespace is set on empty stanzas.
     *
     * @see <a href="https://igniterealtime.org/issues/browse/OF-1087">OF-1087</a>
     */
    @Test
    public void testNamespaceOnEmptyStanza() throws Exception
    {
        // Setup fixture
        final Message message = new Message();
        message.addChildElement( "unittest", "unit:test:namespace" );
        final List<Packet> packets = new ArrayList<>();
        packets.add( message );

        // Execute system under test
        final HttpSession.Deliverable deliverable = new HttpSession.Deliverable( packets );
        final String result = deliverable.getDeliverable();

        // verify results
        // Note that this assertion depends on the Openfire XML parser-specific ordering of attributes.
        assertEquals( "<message xmlns=\"jabber:client\"><unittest xmlns=\"unit:test:namespace\"/></message>", result );
    }

    /**
     * Verifies that the default namespace is set on empty stanzas (that do not have a child element)
     *
     * @see <a href="https://igniterealtime.org/issues/browse/OF-1087">OF-1087</a>
     */
    @Test
    public void testNamespaceOnEmptyStanzaWithoutChildElement() throws Exception
    {
        // Setup fixture
        final Message message = new Message();
        final List<Packet> packets = new ArrayList<>();
        packets.add( message );

        // Execute system under test
        final HttpSession.Deliverable deliverable = new HttpSession.Deliverable( packets );
        final String result = deliverable.getDeliverable();

        // verify results
        // Note that this assertion depends on the Openfire XML parser-specific ordering of attributes.
        assertEquals( "<message xmlns=\"jabber:client\"/>", result );
    }

    /**
     * Verifies that the default namespace is set on (non-empty) stanzas.
     *
     * @see <a href="https://igniterealtime.org/issues/browse/OF-1087">OF-1087</a>
     */
    @Test
    public void testNamespaceOnStanza() throws Exception
    {
        // Setup fixture
        final Message message = new Message();
        message.setTo( "unittest@example.org/test" );
        message.addChildElement( "unittest", "unit:test:namespace" );
        final List<Packet> packets = new ArrayList<>();
        packets.add( message );

        // Execute system under test
        final HttpSession.Deliverable deliverable = new HttpSession.Deliverable( packets );
        final String result = deliverable.getDeliverable();

        // verify results
        // Note that this assertion depends on the Openfire XML parser-specific ordering of attributes.
        assertEquals( "<message to=\"unittest@example.org/test\" xmlns=\"jabber:client\"><unittest xmlns=\"unit:test:namespace\"/></message>", result );
    }

    /**
     * Verifies that the default namespace is not set on stanzas that already have defined a default namespace.
     *
     * @see <a href="https://igniterealtime.org/issues/browse/OF-1087">OF-1087</a>
     */
    @Test
    public void testNamespaceOnStanzaWithNamespace() throws Exception
    {
        // Setup fixture
        final Message message = new Message();
        message.getElement().setQName( QName.get( "message", "unit:test:preexisting:namespace" ) );
        message.setTo( "unittest@example.org/test" );
        message.addChildElement( "unittest", "unit:test:namespace" );
        final List<Packet> packets = new ArrayList<>();
        packets.add( message );

        // Execute system under test
        final HttpSession.Deliverable deliverable = new HttpSession.Deliverable( packets );
        final String result = deliverable.getDeliverable();

        // verify results
        // Note that this assertion depends on the Openfire XML parser-specific ordering of attributes.
        assertEquals( "<message xmlns=\"unit:test:preexisting:namespace\" to=\"unittest@example.org/test\"><unittest xmlns=\"unit:test:namespace\"/></message>", result );
    }
}
