/*
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
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

package com.hazelcast.client.protocol.compatibility;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.ClientMessageReader;
import com.hazelcast.client.impl.protocol.codec.*;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelJVMTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.hazelcast.client.impl.protocol.ClientMessage.IS_FINAL_FLAG;
import static com.hazelcast.client.protocol.compatibility.ReferenceObjects.*;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(HazelcastParallelClassRunner.class)
@Category({QuickTest.class, ParallelJVMTest.class})
public class ClientCompatibilityTest_2_0 {
    private List<ClientMessage> clientMessages = new ArrayList<>();

    @Before
    public void setUp() throws IOException {
        File file = new File(getClass().getResource("/2.0.protocol.compatibility.binary").getFile());
        InputStream inputStream = new FileInputStream(file);
        byte[] data = new byte[(int) file.length()];
        inputStream.read(data);
        ByteBuffer buffer = ByteBuffer.wrap(data);
        ClientMessageReader reader = new ClientMessageReader(0);
        while (reader.readFrom(buffer, true)) {
            clientMessages.add(reader.getClientMessage());
            reader.reset();
        }
    }

    @Test
    public void test_ClientAuthenticationCodec_encodeRequest() {
        int fileClientMessageIndex = 0;
        ClientMessage encoded = ClientAuthenticationCodec.encodeRequest(aString, aString, aString, aUUID, aString, aByte, aString, aString, aListOfStrings);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_ClientAuthenticationCodec_decodeResponse() {
        int fileClientMessageIndex = 1;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        ClientAuthenticationCodec.ResponseParameters parameters = ClientAuthenticationCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aByte, parameters.status));
        assertTrue(isEqual(anAddress, parameters.address));
        assertTrue(isEqual(aUUID, parameters.uuid));
        assertTrue(isEqual(aByte, parameters.serializationVersion));
        assertTrue(isEqual(aString, parameters.serverHazelcastVersion));
        assertTrue(isEqual(anInt, parameters.partitionCount));
        assertTrue(isEqual(aUUID, parameters.clusterId));
    }

    @Test
    public void test_ClientAuthenticationCustomCodec_encodeRequest() {
        int fileClientMessageIndex = 2;
        ClientMessage encoded = ClientAuthenticationCustomCodec.encodeRequest(aString, aData, aUUID, aString, aByte, aString, aString, aListOfStrings);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_ClientAuthenticationCustomCodec_decodeResponse() {
        int fileClientMessageIndex = 3;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        ClientAuthenticationCustomCodec.ResponseParameters parameters = ClientAuthenticationCustomCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aByte, parameters.status));
        assertTrue(isEqual(anAddress, parameters.address));
        assertTrue(isEqual(aUUID, parameters.uuid));
        assertTrue(isEqual(aByte, parameters.serializationVersion));
        assertTrue(isEqual(aString, parameters.serverHazelcastVersion));
        assertTrue(isEqual(anInt, parameters.partitionCount));
        assertTrue(isEqual(aUUID, parameters.clusterId));
    }

    @Test
    public void test_ClientAddClusterViewListenerCodec_encodeRequest() {
        int fileClientMessageIndex = 4;
        ClientMessage encoded = ClientAddClusterViewListenerCodec.encodeRequest(aBoolean);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_ClientAddClusterViewListenerCodec_decodeResponse() {
        int fileClientMessageIndex = 5;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        ClientAddClusterViewListenerCodec.ResponseParameters parameters = ClientAddClusterViewListenerCodec.decodeResponse(fromFile);
    }

    private class ClientAddClusterViewListenerCodecHandler extends ClientAddClusterViewListenerCodec.AbstractEventHandler {
        @Override
        public void handleMembersViewEvent(int version, java.util.Collection<com.hazelcast.internal.cluster.MemberInfo> memberInfos, java.util.Collection<java.util.Map.Entry<com.hazelcast.cluster.Address, java.util.List<java.lang.Integer>>> partitions, int partitionStateVersion) {
            assertTrue(isEqual(anInt, version));
            assertTrue(isEqual(aListOfMemberInfos, memberInfos));
            assertTrue(isEqual(aListOfAddressToListOfIntegers, partitions));
            assertTrue(isEqual(anInt, partitionStateVersion));
        }
        @Override
        public void handleMemberAttributeChangeEvent(com.hazelcast.cluster.Member member, java.lang.String key, int operationType, java.lang.String value) {
            assertTrue(isEqual(aMember, member));
            assertTrue(isEqual(aString, key));
            assertTrue(isEqual(anInt, operationType));
            assertTrue(isEqual(aString, value));
        }
    }

    @Test
    public void test_ClientAddClusterViewListenerCodec_handleMembersViewEvent() {
        int fileClientMessageIndex = 6;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        ClientAddClusterViewListenerCodecHandler handler = new ClientAddClusterViewListenerCodecHandler();
        handler.handle(fromFile);
    }

    @Test
    public void test_ClientAddClusterViewListenerCodec_handleMemberAttributeChangeEvent() {
        int fileClientMessageIndex = 7;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        ClientAddClusterViewListenerCodecHandler handler = new ClientAddClusterViewListenerCodecHandler();
        handler.handle(fromFile);
    }

    @Test
    public void test_ClientCreateProxyCodec_encodeRequest() {
        int fileClientMessageIndex = 8;
        ClientMessage encoded = ClientCreateProxyCodec.encodeRequest(aString, aString);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_ClientCreateProxyCodec_decodeResponse() {
        int fileClientMessageIndex = 9;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        ClientCreateProxyCodec.ResponseParameters parameters = ClientCreateProxyCodec.decodeResponse(fromFile);
    }

    @Test
    public void test_ClientDestroyProxyCodec_encodeRequest() {
        int fileClientMessageIndex = 10;
        ClientMessage encoded = ClientDestroyProxyCodec.encodeRequest(aString, aString);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_ClientDestroyProxyCodec_decodeResponse() {
        int fileClientMessageIndex = 11;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        ClientDestroyProxyCodec.ResponseParameters parameters = ClientDestroyProxyCodec.decodeResponse(fromFile);
    }

    @Test
    public void test_ClientRemoveAllListenersCodec_encodeRequest() {
        int fileClientMessageIndex = 12;
        ClientMessage encoded = ClientRemoveAllListenersCodec.encodeRequest();
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_ClientRemoveAllListenersCodec_decodeResponse() {
        int fileClientMessageIndex = 13;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        ClientRemoveAllListenersCodec.ResponseParameters parameters = ClientRemoveAllListenersCodec.decodeResponse(fromFile);
    }

    @Test
    public void test_ClientAddPartitionLostListenerCodec_encodeRequest() {
        int fileClientMessageIndex = 14;
        ClientMessage encoded = ClientAddPartitionLostListenerCodec.encodeRequest(aBoolean);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_ClientAddPartitionLostListenerCodec_decodeResponse() {
        int fileClientMessageIndex = 15;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        ClientAddPartitionLostListenerCodec.ResponseParameters parameters = ClientAddPartitionLostListenerCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aUUID, parameters.response));
    }

    private class ClientAddPartitionLostListenerCodecHandler extends ClientAddPartitionLostListenerCodec.AbstractEventHandler {
        @Override
        public void handlePartitionLostEvent(int partitionId, int lostBackupCount, com.hazelcast.cluster.Address source) {
            assertTrue(isEqual(anInt, partitionId));
            assertTrue(isEqual(anInt, lostBackupCount));
            assertTrue(isEqual(anAddress, source));
        }
    }

    @Test
    public void test_ClientAddPartitionLostListenerCodec_handlePartitionLostEvent() {
        int fileClientMessageIndex = 16;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        ClientAddPartitionLostListenerCodecHandler handler = new ClientAddPartitionLostListenerCodecHandler();
        handler.handle(fromFile);
    }

    @Test
    public void test_ClientRemovePartitionLostListenerCodec_encodeRequest() {
        int fileClientMessageIndex = 17;
        ClientMessage encoded = ClientRemovePartitionLostListenerCodec.encodeRequest(aUUID);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_ClientRemovePartitionLostListenerCodec_decodeResponse() {
        int fileClientMessageIndex = 18;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        ClientRemovePartitionLostListenerCodec.ResponseParameters parameters = ClientRemovePartitionLostListenerCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aBoolean, parameters.response));
    }

    @Test
    public void test_ClientGetDistributedObjectsCodec_encodeRequest() {
        int fileClientMessageIndex = 19;
        ClientMessage encoded = ClientGetDistributedObjectsCodec.encodeRequest();
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_ClientGetDistributedObjectsCodec_decodeResponse() {
        int fileClientMessageIndex = 20;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        ClientGetDistributedObjectsCodec.ResponseParameters parameters = ClientGetDistributedObjectsCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aListOfDistributedObjectInfo, parameters.response));
    }

    @Test
    public void test_ClientAddDistributedObjectListenerCodec_encodeRequest() {
        int fileClientMessageIndex = 21;
        ClientMessage encoded = ClientAddDistributedObjectListenerCodec.encodeRequest(aBoolean);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_ClientAddDistributedObjectListenerCodec_decodeResponse() {
        int fileClientMessageIndex = 22;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        ClientAddDistributedObjectListenerCodec.ResponseParameters parameters = ClientAddDistributedObjectListenerCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aUUID, parameters.response));
    }

    private class ClientAddDistributedObjectListenerCodecHandler extends ClientAddDistributedObjectListenerCodec.AbstractEventHandler {
        @Override
        public void handleDistributedObjectEvent(java.lang.String name, java.lang.String serviceName, java.lang.String eventType) {
            assertTrue(isEqual(aString, name));
            assertTrue(isEqual(aString, serviceName));
            assertTrue(isEqual(aString, eventType));
        }
    }

    @Test
    public void test_ClientAddDistributedObjectListenerCodec_handleDistributedObjectEvent() {
        int fileClientMessageIndex = 23;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        ClientAddDistributedObjectListenerCodecHandler handler = new ClientAddDistributedObjectListenerCodecHandler();
        handler.handle(fromFile);
    }

    @Test
    public void test_ClientRemoveDistributedObjectListenerCodec_encodeRequest() {
        int fileClientMessageIndex = 24;
        ClientMessage encoded = ClientRemoveDistributedObjectListenerCodec.encodeRequest(aUUID);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_ClientRemoveDistributedObjectListenerCodec_decodeResponse() {
        int fileClientMessageIndex = 25;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        ClientRemoveDistributedObjectListenerCodec.ResponseParameters parameters = ClientRemoveDistributedObjectListenerCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aBoolean, parameters.response));
    }

    @Test
    public void test_ClientPingCodec_encodeRequest() {
        int fileClientMessageIndex = 26;
        ClientMessage encoded = ClientPingCodec.encodeRequest();
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_ClientPingCodec_decodeResponse() {
        int fileClientMessageIndex = 27;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        ClientPingCodec.ResponseParameters parameters = ClientPingCodec.decodeResponse(fromFile);
    }

    @Test
    public void test_ClientStatisticsCodec_encodeRequest() {
        int fileClientMessageIndex = 28;
        ClientMessage encoded = ClientStatisticsCodec.encodeRequest(aLong, aString, aByteArray);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_ClientStatisticsCodec_decodeResponse() {
        int fileClientMessageIndex = 29;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        ClientStatisticsCodec.ResponseParameters parameters = ClientStatisticsCodec.decodeResponse(fromFile);
    }

    @Test
    public void test_ClientDeployClassesCodec_encodeRequest() {
        int fileClientMessageIndex = 30;
        ClientMessage encoded = ClientDeployClassesCodec.encodeRequest(aListOfStringToByteArray);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_ClientDeployClassesCodec_decodeResponse() {
        int fileClientMessageIndex = 31;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        ClientDeployClassesCodec.ResponseParameters parameters = ClientDeployClassesCodec.decodeResponse(fromFile);
    }

    @Test
    public void test_ClientCreateProxiesCodec_encodeRequest() {
        int fileClientMessageIndex = 32;
        ClientMessage encoded = ClientCreateProxiesCodec.encodeRequest(aListOfStringToString);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_ClientCreateProxiesCodec_decodeResponse() {
        int fileClientMessageIndex = 33;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        ClientCreateProxiesCodec.ResponseParameters parameters = ClientCreateProxiesCodec.decodeResponse(fromFile);
    }

    @Test
    public void test_ClientIsFailoverSupportedCodec_encodeRequest() {
        int fileClientMessageIndex = 34;
        ClientMessage encoded = ClientIsFailoverSupportedCodec.encodeRequest();
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_ClientIsFailoverSupportedCodec_decodeResponse() {
        int fileClientMessageIndex = 35;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        ClientIsFailoverSupportedCodec.ResponseParameters parameters = ClientIsFailoverSupportedCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aBoolean, parameters.response));
    }

    @Test
    public void test_ClientLocalBackupListenerCodec_encodeRequest() {
        int fileClientMessageIndex = 36;
        ClientMessage encoded = ClientLocalBackupListenerCodec.encodeRequest();
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_ClientLocalBackupListenerCodec_decodeResponse() {
        int fileClientMessageIndex = 37;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        ClientLocalBackupListenerCodec.ResponseParameters parameters = ClientLocalBackupListenerCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aUUID, parameters.response));
    }

    private class ClientLocalBackupListenerCodecHandler extends ClientLocalBackupListenerCodec.AbstractEventHandler {
        @Override
        public void handleBackupEvent(long sourceInvocationCorrelationId) {
            assertTrue(isEqual(aLong, sourceInvocationCorrelationId));
        }
    }

    @Test
    public void test_ClientLocalBackupListenerCodec_handleBackupEvent() {
        int fileClientMessageIndex = 38;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        ClientLocalBackupListenerCodecHandler handler = new ClientLocalBackupListenerCodecHandler();
        handler.handle(fromFile);
    }

    @Test
    public void test_MapPutCodec_encodeRequest() {
        int fileClientMessageIndex = 39;
        ClientMessage encoded = MapPutCodec.encodeRequest(aString, aData, aData, aLong, aLong);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_MapPutCodec_decodeResponse() {
        int fileClientMessageIndex = 40;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MapPutCodec.ResponseParameters parameters = MapPutCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aData, parameters.response));
    }

    @Test
    public void test_MapGetCodec_encodeRequest() {
        int fileClientMessageIndex = 41;
        ClientMessage encoded = MapGetCodec.encodeRequest(aString, aData, aLong);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_MapGetCodec_decodeResponse() {
        int fileClientMessageIndex = 42;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MapGetCodec.ResponseParameters parameters = MapGetCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aData, parameters.response));
    }

    @Test
    public void test_MapRemoveCodec_encodeRequest() {
        int fileClientMessageIndex = 43;
        ClientMessage encoded = MapRemoveCodec.encodeRequest(aString, aData, aLong);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_MapRemoveCodec_decodeResponse() {
        int fileClientMessageIndex = 44;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MapRemoveCodec.ResponseParameters parameters = MapRemoveCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aData, parameters.response));
    }

    @Test
    public void test_MapReplaceCodec_encodeRequest() {
        int fileClientMessageIndex = 45;
        ClientMessage encoded = MapReplaceCodec.encodeRequest(aString, aData, aData, aLong);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_MapReplaceCodec_decodeResponse() {
        int fileClientMessageIndex = 46;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MapReplaceCodec.ResponseParameters parameters = MapReplaceCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aData, parameters.response));
    }

    @Test
    public void test_MapReplaceIfSameCodec_encodeRequest() {
        int fileClientMessageIndex = 47;
        ClientMessage encoded = MapReplaceIfSameCodec.encodeRequest(aString, aData, aData, aData, aLong);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_MapReplaceIfSameCodec_decodeResponse() {
        int fileClientMessageIndex = 48;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MapReplaceIfSameCodec.ResponseParameters parameters = MapReplaceIfSameCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aBoolean, parameters.response));
    }

    @Test
    public void test_MapContainsKeyCodec_encodeRequest() {
        int fileClientMessageIndex = 49;
        ClientMessage encoded = MapContainsKeyCodec.encodeRequest(aString, aData, aLong);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_MapContainsKeyCodec_decodeResponse() {
        int fileClientMessageIndex = 50;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MapContainsKeyCodec.ResponseParameters parameters = MapContainsKeyCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aBoolean, parameters.response));
    }

    @Test
    public void test_MapContainsValueCodec_encodeRequest() {
        int fileClientMessageIndex = 51;
        ClientMessage encoded = MapContainsValueCodec.encodeRequest(aString, aData);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_MapContainsValueCodec_decodeResponse() {
        int fileClientMessageIndex = 52;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MapContainsValueCodec.ResponseParameters parameters = MapContainsValueCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aBoolean, parameters.response));
    }

    @Test
    public void test_MapRemoveIfSameCodec_encodeRequest() {
        int fileClientMessageIndex = 53;
        ClientMessage encoded = MapRemoveIfSameCodec.encodeRequest(aString, aData, aData, aLong);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_MapRemoveIfSameCodec_decodeResponse() {
        int fileClientMessageIndex = 54;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MapRemoveIfSameCodec.ResponseParameters parameters = MapRemoveIfSameCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aBoolean, parameters.response));
    }

    @Test
    public void test_MapDeleteCodec_encodeRequest() {
        int fileClientMessageIndex = 55;
        ClientMessage encoded = MapDeleteCodec.encodeRequest(aString, aData, aLong);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_MapDeleteCodec_decodeResponse() {
        int fileClientMessageIndex = 56;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MapDeleteCodec.ResponseParameters parameters = MapDeleteCodec.decodeResponse(fromFile);
    }

    @Test
    public void test_MapFlushCodec_encodeRequest() {
        int fileClientMessageIndex = 57;
        ClientMessage encoded = MapFlushCodec.encodeRequest(aString);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_MapFlushCodec_decodeResponse() {
        int fileClientMessageIndex = 58;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MapFlushCodec.ResponseParameters parameters = MapFlushCodec.decodeResponse(fromFile);
    }

    @Test
    public void test_MapTryRemoveCodec_encodeRequest() {
        int fileClientMessageIndex = 59;
        ClientMessage encoded = MapTryRemoveCodec.encodeRequest(aString, aData, aLong, aLong);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_MapTryRemoveCodec_decodeResponse() {
        int fileClientMessageIndex = 60;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MapTryRemoveCodec.ResponseParameters parameters = MapTryRemoveCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aBoolean, parameters.response));
    }

    @Test
    public void test_MapTryPutCodec_encodeRequest() {
        int fileClientMessageIndex = 61;
        ClientMessage encoded = MapTryPutCodec.encodeRequest(aString, aData, aData, aLong, aLong);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_MapTryPutCodec_decodeResponse() {
        int fileClientMessageIndex = 62;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MapTryPutCodec.ResponseParameters parameters = MapTryPutCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aBoolean, parameters.response));
    }

    @Test
    public void test_MapPutTransientCodec_encodeRequest() {
        int fileClientMessageIndex = 63;
        ClientMessage encoded = MapPutTransientCodec.encodeRequest(aString, aData, aData, aLong, aLong);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_MapPutTransientCodec_decodeResponse() {
        int fileClientMessageIndex = 64;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MapPutTransientCodec.ResponseParameters parameters = MapPutTransientCodec.decodeResponse(fromFile);
    }

    @Test
    public void test_MapPutIfAbsentCodec_encodeRequest() {
        int fileClientMessageIndex = 65;
        ClientMessage encoded = MapPutIfAbsentCodec.encodeRequest(aString, aData, aData, aLong, aLong);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_MapPutIfAbsentCodec_decodeResponse() {
        int fileClientMessageIndex = 66;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MapPutIfAbsentCodec.ResponseParameters parameters = MapPutIfAbsentCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aData, parameters.response));
    }

    @Test
    public void test_MapSetCodec_encodeRequest() {
        int fileClientMessageIndex = 67;
        ClientMessage encoded = MapSetCodec.encodeRequest(aString, aData, aData, aLong, aLong);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_MapSetCodec_decodeResponse() {
        int fileClientMessageIndex = 68;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MapSetCodec.ResponseParameters parameters = MapSetCodec.decodeResponse(fromFile);
    }

    @Test
    public void test_MapLockCodec_encodeRequest() {
        int fileClientMessageIndex = 69;
        ClientMessage encoded = MapLockCodec.encodeRequest(aString, aData, aLong, aLong, aLong);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_MapLockCodec_decodeResponse() {
        int fileClientMessageIndex = 70;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MapLockCodec.ResponseParameters parameters = MapLockCodec.decodeResponse(fromFile);
    }

    @Test
    public void test_MapTryLockCodec_encodeRequest() {
        int fileClientMessageIndex = 71;
        ClientMessage encoded = MapTryLockCodec.encodeRequest(aString, aData, aLong, aLong, aLong, aLong);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_MapTryLockCodec_decodeResponse() {
        int fileClientMessageIndex = 72;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MapTryLockCodec.ResponseParameters parameters = MapTryLockCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aBoolean, parameters.response));
    }

    @Test
    public void test_MapIsLockedCodec_encodeRequest() {
        int fileClientMessageIndex = 73;
        ClientMessage encoded = MapIsLockedCodec.encodeRequest(aString, aData);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_MapIsLockedCodec_decodeResponse() {
        int fileClientMessageIndex = 74;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MapIsLockedCodec.ResponseParameters parameters = MapIsLockedCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aBoolean, parameters.response));
    }

    @Test
    public void test_MapUnlockCodec_encodeRequest() {
        int fileClientMessageIndex = 75;
        ClientMessage encoded = MapUnlockCodec.encodeRequest(aString, aData, aLong, aLong);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_MapUnlockCodec_decodeResponse() {
        int fileClientMessageIndex = 76;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MapUnlockCodec.ResponseParameters parameters = MapUnlockCodec.decodeResponse(fromFile);
    }

    @Test
    public void test_MapAddInterceptorCodec_encodeRequest() {
        int fileClientMessageIndex = 77;
        ClientMessage encoded = MapAddInterceptorCodec.encodeRequest(aString, aData);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_MapAddInterceptorCodec_decodeResponse() {
        int fileClientMessageIndex = 78;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MapAddInterceptorCodec.ResponseParameters parameters = MapAddInterceptorCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aString, parameters.response));
    }

    @Test
    public void test_MapRemoveInterceptorCodec_encodeRequest() {
        int fileClientMessageIndex = 79;
        ClientMessage encoded = MapRemoveInterceptorCodec.encodeRequest(aString, aString);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_MapRemoveInterceptorCodec_decodeResponse() {
        int fileClientMessageIndex = 80;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MapRemoveInterceptorCodec.ResponseParameters parameters = MapRemoveInterceptorCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aBoolean, parameters.response));
    }

    @Test
    public void test_MapAddEntryListenerToKeyWithPredicateCodec_encodeRequest() {
        int fileClientMessageIndex = 81;
        ClientMessage encoded = MapAddEntryListenerToKeyWithPredicateCodec.encodeRequest(aString, aData, aData, aBoolean, anInt, aBoolean);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_MapAddEntryListenerToKeyWithPredicateCodec_decodeResponse() {
        int fileClientMessageIndex = 82;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MapAddEntryListenerToKeyWithPredicateCodec.ResponseParameters parameters = MapAddEntryListenerToKeyWithPredicateCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aUUID, parameters.response));
    }

    private class MapAddEntryListenerToKeyWithPredicateCodecHandler extends MapAddEntryListenerToKeyWithPredicateCodec.AbstractEventHandler {
        @Override
        public void handleEntryEvent(com.hazelcast.nio.serialization.Data key, com.hazelcast.nio.serialization.Data value, com.hazelcast.nio.serialization.Data oldValue, com.hazelcast.nio.serialization.Data mergingValue, int eventType, java.util.UUID uuid, int numberOfAffectedEntries) {
            assertTrue(isEqual(aData, key));
            assertTrue(isEqual(aData, value));
            assertTrue(isEqual(aData, oldValue));
            assertTrue(isEqual(aData, mergingValue));
            assertTrue(isEqual(anInt, eventType));
            assertTrue(isEqual(aUUID, uuid));
            assertTrue(isEqual(anInt, numberOfAffectedEntries));
        }
    }

    @Test
    public void test_MapAddEntryListenerToKeyWithPredicateCodec_handleEntryEvent() {
        int fileClientMessageIndex = 83;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MapAddEntryListenerToKeyWithPredicateCodecHandler handler = new MapAddEntryListenerToKeyWithPredicateCodecHandler();
        handler.handle(fromFile);
    }

    @Test
    public void test_MapAddEntryListenerWithPredicateCodec_encodeRequest() {
        int fileClientMessageIndex = 84;
        ClientMessage encoded = MapAddEntryListenerWithPredicateCodec.encodeRequest(aString, aData, aBoolean, anInt, aBoolean);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_MapAddEntryListenerWithPredicateCodec_decodeResponse() {
        int fileClientMessageIndex = 85;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MapAddEntryListenerWithPredicateCodec.ResponseParameters parameters = MapAddEntryListenerWithPredicateCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aUUID, parameters.response));
    }

    private class MapAddEntryListenerWithPredicateCodecHandler extends MapAddEntryListenerWithPredicateCodec.AbstractEventHandler {
        @Override
        public void handleEntryEvent(com.hazelcast.nio.serialization.Data key, com.hazelcast.nio.serialization.Data value, com.hazelcast.nio.serialization.Data oldValue, com.hazelcast.nio.serialization.Data mergingValue, int eventType, java.util.UUID uuid, int numberOfAffectedEntries) {
            assertTrue(isEqual(aData, key));
            assertTrue(isEqual(aData, value));
            assertTrue(isEqual(aData, oldValue));
            assertTrue(isEqual(aData, mergingValue));
            assertTrue(isEqual(anInt, eventType));
            assertTrue(isEqual(aUUID, uuid));
            assertTrue(isEqual(anInt, numberOfAffectedEntries));
        }
    }

    @Test
    public void test_MapAddEntryListenerWithPredicateCodec_handleEntryEvent() {
        int fileClientMessageIndex = 86;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MapAddEntryListenerWithPredicateCodecHandler handler = new MapAddEntryListenerWithPredicateCodecHandler();
        handler.handle(fromFile);
    }

    @Test
    public void test_MapAddEntryListenerToKeyCodec_encodeRequest() {
        int fileClientMessageIndex = 87;
        ClientMessage encoded = MapAddEntryListenerToKeyCodec.encodeRequest(aString, aData, aBoolean, anInt, aBoolean);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_MapAddEntryListenerToKeyCodec_decodeResponse() {
        int fileClientMessageIndex = 88;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MapAddEntryListenerToKeyCodec.ResponseParameters parameters = MapAddEntryListenerToKeyCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aUUID, parameters.response));
    }

    private class MapAddEntryListenerToKeyCodecHandler extends MapAddEntryListenerToKeyCodec.AbstractEventHandler {
        @Override
        public void handleEntryEvent(com.hazelcast.nio.serialization.Data key, com.hazelcast.nio.serialization.Data value, com.hazelcast.nio.serialization.Data oldValue, com.hazelcast.nio.serialization.Data mergingValue, int eventType, java.util.UUID uuid, int numberOfAffectedEntries) {
            assertTrue(isEqual(aData, key));
            assertTrue(isEqual(aData, value));
            assertTrue(isEqual(aData, oldValue));
            assertTrue(isEqual(aData, mergingValue));
            assertTrue(isEqual(anInt, eventType));
            assertTrue(isEqual(aUUID, uuid));
            assertTrue(isEqual(anInt, numberOfAffectedEntries));
        }
    }

    @Test
    public void test_MapAddEntryListenerToKeyCodec_handleEntryEvent() {
        int fileClientMessageIndex = 89;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MapAddEntryListenerToKeyCodecHandler handler = new MapAddEntryListenerToKeyCodecHandler();
        handler.handle(fromFile);
    }

    @Test
    public void test_MapAddEntryListenerCodec_encodeRequest() {
        int fileClientMessageIndex = 90;
        ClientMessage encoded = MapAddEntryListenerCodec.encodeRequest(aString, aBoolean, anInt, aBoolean);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_MapAddEntryListenerCodec_decodeResponse() {
        int fileClientMessageIndex = 91;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MapAddEntryListenerCodec.ResponseParameters parameters = MapAddEntryListenerCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aUUID, parameters.response));
    }

    private class MapAddEntryListenerCodecHandler extends MapAddEntryListenerCodec.AbstractEventHandler {
        @Override
        public void handleEntryEvent(com.hazelcast.nio.serialization.Data key, com.hazelcast.nio.serialization.Data value, com.hazelcast.nio.serialization.Data oldValue, com.hazelcast.nio.serialization.Data mergingValue, int eventType, java.util.UUID uuid, int numberOfAffectedEntries) {
            assertTrue(isEqual(aData, key));
            assertTrue(isEqual(aData, value));
            assertTrue(isEqual(aData, oldValue));
            assertTrue(isEqual(aData, mergingValue));
            assertTrue(isEqual(anInt, eventType));
            assertTrue(isEqual(aUUID, uuid));
            assertTrue(isEqual(anInt, numberOfAffectedEntries));
        }
    }

    @Test
    public void test_MapAddEntryListenerCodec_handleEntryEvent() {
        int fileClientMessageIndex = 92;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MapAddEntryListenerCodecHandler handler = new MapAddEntryListenerCodecHandler();
        handler.handle(fromFile);
    }

    @Test
    public void test_MapRemoveEntryListenerCodec_encodeRequest() {
        int fileClientMessageIndex = 93;
        ClientMessage encoded = MapRemoveEntryListenerCodec.encodeRequest(aString, aUUID);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_MapRemoveEntryListenerCodec_decodeResponse() {
        int fileClientMessageIndex = 94;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MapRemoveEntryListenerCodec.ResponseParameters parameters = MapRemoveEntryListenerCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aBoolean, parameters.response));
    }

    @Test
    public void test_MapAddPartitionLostListenerCodec_encodeRequest() {
        int fileClientMessageIndex = 95;
        ClientMessage encoded = MapAddPartitionLostListenerCodec.encodeRequest(aString, aBoolean);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_MapAddPartitionLostListenerCodec_decodeResponse() {
        int fileClientMessageIndex = 96;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MapAddPartitionLostListenerCodec.ResponseParameters parameters = MapAddPartitionLostListenerCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aUUID, parameters.response));
    }

    private class MapAddPartitionLostListenerCodecHandler extends MapAddPartitionLostListenerCodec.AbstractEventHandler {
        @Override
        public void handleMapPartitionLostEvent(int partitionId, java.util.UUID uuid) {
            assertTrue(isEqual(anInt, partitionId));
            assertTrue(isEqual(aUUID, uuid));
        }
    }

    @Test
    public void test_MapAddPartitionLostListenerCodec_handleMapPartitionLostEvent() {
        int fileClientMessageIndex = 97;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MapAddPartitionLostListenerCodecHandler handler = new MapAddPartitionLostListenerCodecHandler();
        handler.handle(fromFile);
    }

    @Test
    public void test_MapRemovePartitionLostListenerCodec_encodeRequest() {
        int fileClientMessageIndex = 98;
        ClientMessage encoded = MapRemovePartitionLostListenerCodec.encodeRequest(aString, aUUID);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_MapRemovePartitionLostListenerCodec_decodeResponse() {
        int fileClientMessageIndex = 99;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MapRemovePartitionLostListenerCodec.ResponseParameters parameters = MapRemovePartitionLostListenerCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aBoolean, parameters.response));
    }

    @Test
    public void test_MapGetEntryViewCodec_encodeRequest() {
        int fileClientMessageIndex = 100;
        ClientMessage encoded = MapGetEntryViewCodec.encodeRequest(aString, aData, aLong);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_MapGetEntryViewCodec_decodeResponse() {
        int fileClientMessageIndex = 101;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MapGetEntryViewCodec.ResponseParameters parameters = MapGetEntryViewCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aSimpleEntryView, parameters.response));
        assertTrue(isEqual(aLong, parameters.maxIdle));
    }

    @Test
    public void test_MapEvictCodec_encodeRequest() {
        int fileClientMessageIndex = 102;
        ClientMessage encoded = MapEvictCodec.encodeRequest(aString, aData, aLong);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_MapEvictCodec_decodeResponse() {
        int fileClientMessageIndex = 103;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MapEvictCodec.ResponseParameters parameters = MapEvictCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aBoolean, parameters.response));
    }

    @Test
    public void test_MapEvictAllCodec_encodeRequest() {
        int fileClientMessageIndex = 104;
        ClientMessage encoded = MapEvictAllCodec.encodeRequest(aString);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_MapEvictAllCodec_decodeResponse() {
        int fileClientMessageIndex = 105;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MapEvictAllCodec.ResponseParameters parameters = MapEvictAllCodec.decodeResponse(fromFile);
    }

    @Test
    public void test_MapLoadAllCodec_encodeRequest() {
        int fileClientMessageIndex = 106;
        ClientMessage encoded = MapLoadAllCodec.encodeRequest(aString, aBoolean);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_MapLoadAllCodec_decodeResponse() {
        int fileClientMessageIndex = 107;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MapLoadAllCodec.ResponseParameters parameters = MapLoadAllCodec.decodeResponse(fromFile);
    }

    @Test
    public void test_MapLoadGivenKeysCodec_encodeRequest() {
        int fileClientMessageIndex = 108;
        ClientMessage encoded = MapLoadGivenKeysCodec.encodeRequest(aString, aListOfData, aBoolean);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_MapLoadGivenKeysCodec_decodeResponse() {
        int fileClientMessageIndex = 109;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MapLoadGivenKeysCodec.ResponseParameters parameters = MapLoadGivenKeysCodec.decodeResponse(fromFile);
    }

    @Test
    public void test_MapKeySetCodec_encodeRequest() {
        int fileClientMessageIndex = 110;
        ClientMessage encoded = MapKeySetCodec.encodeRequest(aString);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_MapKeySetCodec_decodeResponse() {
        int fileClientMessageIndex = 111;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MapKeySetCodec.ResponseParameters parameters = MapKeySetCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aListOfData, parameters.response));
    }

    @Test
    public void test_MapGetAllCodec_encodeRequest() {
        int fileClientMessageIndex = 112;
        ClientMessage encoded = MapGetAllCodec.encodeRequest(aString, aListOfData);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_MapGetAllCodec_decodeResponse() {
        int fileClientMessageIndex = 113;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MapGetAllCodec.ResponseParameters parameters = MapGetAllCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aListOfDataToData, parameters.response));
    }

    @Test
    public void test_MapValuesCodec_encodeRequest() {
        int fileClientMessageIndex = 114;
        ClientMessage encoded = MapValuesCodec.encodeRequest(aString);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_MapValuesCodec_decodeResponse() {
        int fileClientMessageIndex = 115;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MapValuesCodec.ResponseParameters parameters = MapValuesCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aListOfData, parameters.response));
    }

    @Test
    public void test_MapEntrySetCodec_encodeRequest() {
        int fileClientMessageIndex = 116;
        ClientMessage encoded = MapEntrySetCodec.encodeRequest(aString);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_MapEntrySetCodec_decodeResponse() {
        int fileClientMessageIndex = 117;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MapEntrySetCodec.ResponseParameters parameters = MapEntrySetCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aListOfDataToData, parameters.response));
    }

    @Test
    public void test_MapKeySetWithPredicateCodec_encodeRequest() {
        int fileClientMessageIndex = 118;
        ClientMessage encoded = MapKeySetWithPredicateCodec.encodeRequest(aString, aData);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_MapKeySetWithPredicateCodec_decodeResponse() {
        int fileClientMessageIndex = 119;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MapKeySetWithPredicateCodec.ResponseParameters parameters = MapKeySetWithPredicateCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aListOfData, parameters.response));
    }

    @Test
    public void test_MapValuesWithPredicateCodec_encodeRequest() {
        int fileClientMessageIndex = 120;
        ClientMessage encoded = MapValuesWithPredicateCodec.encodeRequest(aString, aData);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_MapValuesWithPredicateCodec_decodeResponse() {
        int fileClientMessageIndex = 121;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MapValuesWithPredicateCodec.ResponseParameters parameters = MapValuesWithPredicateCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aListOfData, parameters.response));
    }

    @Test
    public void test_MapEntriesWithPredicateCodec_encodeRequest() {
        int fileClientMessageIndex = 122;
        ClientMessage encoded = MapEntriesWithPredicateCodec.encodeRequest(aString, aData);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_MapEntriesWithPredicateCodec_decodeResponse() {
        int fileClientMessageIndex = 123;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MapEntriesWithPredicateCodec.ResponseParameters parameters = MapEntriesWithPredicateCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aListOfDataToData, parameters.response));
    }

    @Test
    public void test_MapAddIndexCodec_encodeRequest() {
        int fileClientMessageIndex = 124;
        ClientMessage encoded = MapAddIndexCodec.encodeRequest(aString, anIndexConfig);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_MapAddIndexCodec_decodeResponse() {
        int fileClientMessageIndex = 125;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MapAddIndexCodec.ResponseParameters parameters = MapAddIndexCodec.decodeResponse(fromFile);
    }

    @Test
    public void test_MapSizeCodec_encodeRequest() {
        int fileClientMessageIndex = 126;
        ClientMessage encoded = MapSizeCodec.encodeRequest(aString);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_MapSizeCodec_decodeResponse() {
        int fileClientMessageIndex = 127;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MapSizeCodec.ResponseParameters parameters = MapSizeCodec.decodeResponse(fromFile);
        assertTrue(isEqual(anInt, parameters.response));
    }

    @Test
    public void test_MapIsEmptyCodec_encodeRequest() {
        int fileClientMessageIndex = 128;
        ClientMessage encoded = MapIsEmptyCodec.encodeRequest(aString);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_MapIsEmptyCodec_decodeResponse() {
        int fileClientMessageIndex = 129;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MapIsEmptyCodec.ResponseParameters parameters = MapIsEmptyCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aBoolean, parameters.response));
    }

    @Test
    public void test_MapPutAllCodec_encodeRequest() {
        int fileClientMessageIndex = 130;
        ClientMessage encoded = MapPutAllCodec.encodeRequest(aString, aListOfDataToData);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_MapPutAllCodec_decodeResponse() {
        int fileClientMessageIndex = 131;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MapPutAllCodec.ResponseParameters parameters = MapPutAllCodec.decodeResponse(fromFile);
    }

    @Test
    public void test_MapClearCodec_encodeRequest() {
        int fileClientMessageIndex = 132;
        ClientMessage encoded = MapClearCodec.encodeRequest(aString);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_MapClearCodec_decodeResponse() {
        int fileClientMessageIndex = 133;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MapClearCodec.ResponseParameters parameters = MapClearCodec.decodeResponse(fromFile);
    }

    @Test
    public void test_MapExecuteOnKeyCodec_encodeRequest() {
        int fileClientMessageIndex = 134;
        ClientMessage encoded = MapExecuteOnKeyCodec.encodeRequest(aString, aData, aData, aLong);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_MapExecuteOnKeyCodec_decodeResponse() {
        int fileClientMessageIndex = 135;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MapExecuteOnKeyCodec.ResponseParameters parameters = MapExecuteOnKeyCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aData, parameters.response));
    }

    @Test
    public void test_MapSubmitToKeyCodec_encodeRequest() {
        int fileClientMessageIndex = 136;
        ClientMessage encoded = MapSubmitToKeyCodec.encodeRequest(aString, aData, aData, aLong);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_MapSubmitToKeyCodec_decodeResponse() {
        int fileClientMessageIndex = 137;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MapSubmitToKeyCodec.ResponseParameters parameters = MapSubmitToKeyCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aData, parameters.response));
    }

    @Test
    public void test_MapExecuteOnAllKeysCodec_encodeRequest() {
        int fileClientMessageIndex = 138;
        ClientMessage encoded = MapExecuteOnAllKeysCodec.encodeRequest(aString, aData);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_MapExecuteOnAllKeysCodec_decodeResponse() {
        int fileClientMessageIndex = 139;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MapExecuteOnAllKeysCodec.ResponseParameters parameters = MapExecuteOnAllKeysCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aListOfDataToData, parameters.response));
    }

    @Test
    public void test_MapExecuteWithPredicateCodec_encodeRequest() {
        int fileClientMessageIndex = 140;
        ClientMessage encoded = MapExecuteWithPredicateCodec.encodeRequest(aString, aData, aData);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_MapExecuteWithPredicateCodec_decodeResponse() {
        int fileClientMessageIndex = 141;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MapExecuteWithPredicateCodec.ResponseParameters parameters = MapExecuteWithPredicateCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aListOfDataToData, parameters.response));
    }

    @Test
    public void test_MapExecuteOnKeysCodec_encodeRequest() {
        int fileClientMessageIndex = 142;
        ClientMessage encoded = MapExecuteOnKeysCodec.encodeRequest(aString, aData, aListOfData);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_MapExecuteOnKeysCodec_decodeResponse() {
        int fileClientMessageIndex = 143;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MapExecuteOnKeysCodec.ResponseParameters parameters = MapExecuteOnKeysCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aListOfDataToData, parameters.response));
    }

    @Test
    public void test_MapForceUnlockCodec_encodeRequest() {
        int fileClientMessageIndex = 144;
        ClientMessage encoded = MapForceUnlockCodec.encodeRequest(aString, aData, aLong);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_MapForceUnlockCodec_decodeResponse() {
        int fileClientMessageIndex = 145;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MapForceUnlockCodec.ResponseParameters parameters = MapForceUnlockCodec.decodeResponse(fromFile);
    }

    @Test
    public void test_MapKeySetWithPagingPredicateCodec_encodeRequest() {
        int fileClientMessageIndex = 146;
        ClientMessage encoded = MapKeySetWithPagingPredicateCodec.encodeRequest(aString, aData);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_MapKeySetWithPagingPredicateCodec_decodeResponse() {
        int fileClientMessageIndex = 147;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MapKeySetWithPagingPredicateCodec.ResponseParameters parameters = MapKeySetWithPagingPredicateCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aListOfData, parameters.response));
    }

    @Test
    public void test_MapValuesWithPagingPredicateCodec_encodeRequest() {
        int fileClientMessageIndex = 148;
        ClientMessage encoded = MapValuesWithPagingPredicateCodec.encodeRequest(aString, aData);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_MapValuesWithPagingPredicateCodec_decodeResponse() {
        int fileClientMessageIndex = 149;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MapValuesWithPagingPredicateCodec.ResponseParameters parameters = MapValuesWithPagingPredicateCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aListOfDataToData, parameters.response));
    }

    @Test
    public void test_MapEntriesWithPagingPredicateCodec_encodeRequest() {
        int fileClientMessageIndex = 150;
        ClientMessage encoded = MapEntriesWithPagingPredicateCodec.encodeRequest(aString, aData);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_MapEntriesWithPagingPredicateCodec_decodeResponse() {
        int fileClientMessageIndex = 151;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MapEntriesWithPagingPredicateCodec.ResponseParameters parameters = MapEntriesWithPagingPredicateCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aListOfDataToData, parameters.response));
    }

    @Test
    public void test_MapClearNearCacheCodec_encodeRequest() {
        int fileClientMessageIndex = 152;
        ClientMessage encoded = MapClearNearCacheCodec.encodeRequest(aString, anAddress);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_MapClearNearCacheCodec_decodeResponse() {
        int fileClientMessageIndex = 153;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MapClearNearCacheCodec.ResponseParameters parameters = MapClearNearCacheCodec.decodeResponse(fromFile);
    }

    @Test
    public void test_MapFetchKeysCodec_encodeRequest() {
        int fileClientMessageIndex = 154;
        ClientMessage encoded = MapFetchKeysCodec.encodeRequest(aString, anInt, anInt);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_MapFetchKeysCodec_decodeResponse() {
        int fileClientMessageIndex = 155;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MapFetchKeysCodec.ResponseParameters parameters = MapFetchKeysCodec.decodeResponse(fromFile);
        assertTrue(isEqual(anInt, parameters.tableIndex));
        assertTrue(isEqual(aListOfData, parameters.keys));
    }

    @Test
    public void test_MapFetchEntriesCodec_encodeRequest() {
        int fileClientMessageIndex = 156;
        ClientMessage encoded = MapFetchEntriesCodec.encodeRequest(aString, anInt, anInt);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_MapFetchEntriesCodec_decodeResponse() {
        int fileClientMessageIndex = 157;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MapFetchEntriesCodec.ResponseParameters parameters = MapFetchEntriesCodec.decodeResponse(fromFile);
        assertTrue(isEqual(anInt, parameters.tableIndex));
        assertTrue(isEqual(aListOfDataToData, parameters.entries));
    }

    @Test
    public void test_MapAggregateCodec_encodeRequest() {
        int fileClientMessageIndex = 158;
        ClientMessage encoded = MapAggregateCodec.encodeRequest(aString, aData);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_MapAggregateCodec_decodeResponse() {
        int fileClientMessageIndex = 159;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MapAggregateCodec.ResponseParameters parameters = MapAggregateCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aData, parameters.response));
    }

    @Test
    public void test_MapAggregateWithPredicateCodec_encodeRequest() {
        int fileClientMessageIndex = 160;
        ClientMessage encoded = MapAggregateWithPredicateCodec.encodeRequest(aString, aData, aData);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_MapAggregateWithPredicateCodec_decodeResponse() {
        int fileClientMessageIndex = 161;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MapAggregateWithPredicateCodec.ResponseParameters parameters = MapAggregateWithPredicateCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aData, parameters.response));
    }

    @Test
    public void test_MapProjectCodec_encodeRequest() {
        int fileClientMessageIndex = 162;
        ClientMessage encoded = MapProjectCodec.encodeRequest(aString, aData);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_MapProjectCodec_decodeResponse() {
        int fileClientMessageIndex = 163;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MapProjectCodec.ResponseParameters parameters = MapProjectCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aListOfData, parameters.response));
    }

    @Test
    public void test_MapProjectWithPredicateCodec_encodeRequest() {
        int fileClientMessageIndex = 164;
        ClientMessage encoded = MapProjectWithPredicateCodec.encodeRequest(aString, aData, aData);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_MapProjectWithPredicateCodec_decodeResponse() {
        int fileClientMessageIndex = 165;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MapProjectWithPredicateCodec.ResponseParameters parameters = MapProjectWithPredicateCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aListOfData, parameters.response));
    }

    @Test
    public void test_MapFetchNearCacheInvalidationMetadataCodec_encodeRequest() {
        int fileClientMessageIndex = 166;
        ClientMessage encoded = MapFetchNearCacheInvalidationMetadataCodec.encodeRequest(aListOfStrings, anAddress);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_MapFetchNearCacheInvalidationMetadataCodec_decodeResponse() {
        int fileClientMessageIndex = 167;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MapFetchNearCacheInvalidationMetadataCodec.ResponseParameters parameters = MapFetchNearCacheInvalidationMetadataCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aListOfStringToListOfIntegerToLong, parameters.namePartitionSequenceList));
        assertTrue(isEqual(aListOfIntegerToUUID, parameters.partitionUuidList));
    }

    @Test
    public void test_MapAssignAndGetUuidsCodec_encodeRequest() {
        int fileClientMessageIndex = 168;
        ClientMessage encoded = MapAssignAndGetUuidsCodec.encodeRequest();
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_MapAssignAndGetUuidsCodec_decodeResponse() {
        int fileClientMessageIndex = 169;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MapAssignAndGetUuidsCodec.ResponseParameters parameters = MapAssignAndGetUuidsCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aListOfIntegerToUUID, parameters.partitionUuidList));
    }

    @Test
    public void test_MapRemoveAllCodec_encodeRequest() {
        int fileClientMessageIndex = 170;
        ClientMessage encoded = MapRemoveAllCodec.encodeRequest(aString, aData);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_MapRemoveAllCodec_decodeResponse() {
        int fileClientMessageIndex = 171;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MapRemoveAllCodec.ResponseParameters parameters = MapRemoveAllCodec.decodeResponse(fromFile);
    }

    @Test
    public void test_MapAddNearCacheInvalidationListenerCodec_encodeRequest() {
        int fileClientMessageIndex = 172;
        ClientMessage encoded = MapAddNearCacheInvalidationListenerCodec.encodeRequest(aString, anInt, aBoolean);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_MapAddNearCacheInvalidationListenerCodec_decodeResponse() {
        int fileClientMessageIndex = 173;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MapAddNearCacheInvalidationListenerCodec.ResponseParameters parameters = MapAddNearCacheInvalidationListenerCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aUUID, parameters.response));
    }

    private class MapAddNearCacheInvalidationListenerCodecHandler extends MapAddNearCacheInvalidationListenerCodec.AbstractEventHandler {
        @Override
        public void handleIMapInvalidationEvent(com.hazelcast.nio.serialization.Data key, java.util.UUID sourceUuid, java.util.UUID partitionUuid, long sequence) {
            assertTrue(isEqual(aData, key));
            assertTrue(isEqual(aUUID, sourceUuid));
            assertTrue(isEqual(aUUID, partitionUuid));
            assertTrue(isEqual(aLong, sequence));
        }
        @Override
        public void handleIMapBatchInvalidationEvent(java.util.Collection<com.hazelcast.nio.serialization.Data> keys, java.util.Collection<java.util.UUID> sourceUuids, java.util.Collection<java.util.UUID> partitionUuids, java.util.Collection<java.lang.Long> sequences) {
            assertTrue(isEqual(aListOfData, keys));
            assertTrue(isEqual(aListOfUUIDs, sourceUuids));
            assertTrue(isEqual(aListOfUUIDs, partitionUuids));
            assertTrue(isEqual(aListOfLongs, sequences));
        }
    }

    @Test
    public void test_MapAddNearCacheInvalidationListenerCodec_handleIMapInvalidationEvent() {
        int fileClientMessageIndex = 174;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MapAddNearCacheInvalidationListenerCodecHandler handler = new MapAddNearCacheInvalidationListenerCodecHandler();
        handler.handle(fromFile);
    }

    @Test
    public void test_MapAddNearCacheInvalidationListenerCodec_handleIMapBatchInvalidationEvent() {
        int fileClientMessageIndex = 175;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MapAddNearCacheInvalidationListenerCodecHandler handler = new MapAddNearCacheInvalidationListenerCodecHandler();
        handler.handle(fromFile);
    }

    @Test
    public void test_MapFetchWithQueryCodec_encodeRequest() {
        int fileClientMessageIndex = 176;
        ClientMessage encoded = MapFetchWithQueryCodec.encodeRequest(aString, anInt, anInt, aData, aData);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_MapFetchWithQueryCodec_decodeResponse() {
        int fileClientMessageIndex = 177;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MapFetchWithQueryCodec.ResponseParameters parameters = MapFetchWithQueryCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aListOfData, parameters.results));
        assertTrue(isEqual(anInt, parameters.nextTableIndexToReadFrom));
    }

    @Test
    public void test_MapEventJournalSubscribeCodec_encodeRequest() {
        int fileClientMessageIndex = 178;
        ClientMessage encoded = MapEventJournalSubscribeCodec.encodeRequest(aString);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_MapEventJournalSubscribeCodec_decodeResponse() {
        int fileClientMessageIndex = 179;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MapEventJournalSubscribeCodec.ResponseParameters parameters = MapEventJournalSubscribeCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aLong, parameters.oldestSequence));
        assertTrue(isEqual(aLong, parameters.newestSequence));
    }

    @Test
    public void test_MapEventJournalReadCodec_encodeRequest() {
        int fileClientMessageIndex = 180;
        ClientMessage encoded = MapEventJournalReadCodec.encodeRequest(aString, aLong, anInt, anInt, aData, aData);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_MapEventJournalReadCodec_decodeResponse() {
        int fileClientMessageIndex = 181;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MapEventJournalReadCodec.ResponseParameters parameters = MapEventJournalReadCodec.decodeResponse(fromFile);
        assertTrue(isEqual(anInt, parameters.readCount));
        assertTrue(isEqual(aListOfData, parameters.items));
        assertTrue(isEqual(aLongArray, parameters.itemSeqs));
        assertTrue(isEqual(aLong, parameters.nextSeq));
    }

    @Test
    public void test_MapSetTtlCodec_encodeRequest() {
        int fileClientMessageIndex = 182;
        ClientMessage encoded = MapSetTtlCodec.encodeRequest(aString, aData, aLong);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_MapSetTtlCodec_decodeResponse() {
        int fileClientMessageIndex = 183;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MapSetTtlCodec.ResponseParameters parameters = MapSetTtlCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aBoolean, parameters.response));
    }

    @Test
    public void test_MapPutWithMaxIdleCodec_encodeRequest() {
        int fileClientMessageIndex = 184;
        ClientMessage encoded = MapPutWithMaxIdleCodec.encodeRequest(aString, aData, aData, aLong, aLong, aLong);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_MapPutWithMaxIdleCodec_decodeResponse() {
        int fileClientMessageIndex = 185;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MapPutWithMaxIdleCodec.ResponseParameters parameters = MapPutWithMaxIdleCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aData, parameters.response));
    }

    @Test
    public void test_MapPutTransientWithMaxIdleCodec_encodeRequest() {
        int fileClientMessageIndex = 186;
        ClientMessage encoded = MapPutTransientWithMaxIdleCodec.encodeRequest(aString, aData, aData, aLong, aLong, aLong);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_MapPutTransientWithMaxIdleCodec_decodeResponse() {
        int fileClientMessageIndex = 187;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MapPutTransientWithMaxIdleCodec.ResponseParameters parameters = MapPutTransientWithMaxIdleCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aData, parameters.response));
    }

    @Test
    public void test_MapPutIfAbsentWithMaxIdleCodec_encodeRequest() {
        int fileClientMessageIndex = 188;
        ClientMessage encoded = MapPutIfAbsentWithMaxIdleCodec.encodeRequest(aString, aData, aData, aLong, aLong, aLong);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_MapPutIfAbsentWithMaxIdleCodec_decodeResponse() {
        int fileClientMessageIndex = 189;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MapPutIfAbsentWithMaxIdleCodec.ResponseParameters parameters = MapPutIfAbsentWithMaxIdleCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aData, parameters.response));
    }

    @Test
    public void test_MapSetWithMaxIdleCodec_encodeRequest() {
        int fileClientMessageIndex = 190;
        ClientMessage encoded = MapSetWithMaxIdleCodec.encodeRequest(aString, aData, aData, aLong, aLong, aLong);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_MapSetWithMaxIdleCodec_decodeResponse() {
        int fileClientMessageIndex = 191;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MapSetWithMaxIdleCodec.ResponseParameters parameters = MapSetWithMaxIdleCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aData, parameters.response));
    }

    @Test
    public void test_MultiMapPutCodec_encodeRequest() {
        int fileClientMessageIndex = 192;
        ClientMessage encoded = MultiMapPutCodec.encodeRequest(aString, aData, aData, aLong);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_MultiMapPutCodec_decodeResponse() {
        int fileClientMessageIndex = 193;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MultiMapPutCodec.ResponseParameters parameters = MultiMapPutCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aBoolean, parameters.response));
    }

    @Test
    public void test_MultiMapGetCodec_encodeRequest() {
        int fileClientMessageIndex = 194;
        ClientMessage encoded = MultiMapGetCodec.encodeRequest(aString, aData, aLong);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_MultiMapGetCodec_decodeResponse() {
        int fileClientMessageIndex = 195;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MultiMapGetCodec.ResponseParameters parameters = MultiMapGetCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aListOfData, parameters.response));
    }

    @Test
    public void test_MultiMapRemoveCodec_encodeRequest() {
        int fileClientMessageIndex = 196;
        ClientMessage encoded = MultiMapRemoveCodec.encodeRequest(aString, aData, aLong);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_MultiMapRemoveCodec_decodeResponse() {
        int fileClientMessageIndex = 197;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MultiMapRemoveCodec.ResponseParameters parameters = MultiMapRemoveCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aListOfData, parameters.response));
    }

    @Test
    public void test_MultiMapKeySetCodec_encodeRequest() {
        int fileClientMessageIndex = 198;
        ClientMessage encoded = MultiMapKeySetCodec.encodeRequest(aString);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_MultiMapKeySetCodec_decodeResponse() {
        int fileClientMessageIndex = 199;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MultiMapKeySetCodec.ResponseParameters parameters = MultiMapKeySetCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aListOfData, parameters.response));
    }

    @Test
    public void test_MultiMapValuesCodec_encodeRequest() {
        int fileClientMessageIndex = 200;
        ClientMessage encoded = MultiMapValuesCodec.encodeRequest(aString);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_MultiMapValuesCodec_decodeResponse() {
        int fileClientMessageIndex = 201;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MultiMapValuesCodec.ResponseParameters parameters = MultiMapValuesCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aListOfData, parameters.response));
    }

    @Test
    public void test_MultiMapEntrySetCodec_encodeRequest() {
        int fileClientMessageIndex = 202;
        ClientMessage encoded = MultiMapEntrySetCodec.encodeRequest(aString);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_MultiMapEntrySetCodec_decodeResponse() {
        int fileClientMessageIndex = 203;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MultiMapEntrySetCodec.ResponseParameters parameters = MultiMapEntrySetCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aListOfDataToData, parameters.response));
    }

    @Test
    public void test_MultiMapContainsKeyCodec_encodeRequest() {
        int fileClientMessageIndex = 204;
        ClientMessage encoded = MultiMapContainsKeyCodec.encodeRequest(aString, aData, aLong);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_MultiMapContainsKeyCodec_decodeResponse() {
        int fileClientMessageIndex = 205;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MultiMapContainsKeyCodec.ResponseParameters parameters = MultiMapContainsKeyCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aBoolean, parameters.response));
    }

    @Test
    public void test_MultiMapContainsValueCodec_encodeRequest() {
        int fileClientMessageIndex = 206;
        ClientMessage encoded = MultiMapContainsValueCodec.encodeRequest(aString, aData);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_MultiMapContainsValueCodec_decodeResponse() {
        int fileClientMessageIndex = 207;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MultiMapContainsValueCodec.ResponseParameters parameters = MultiMapContainsValueCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aBoolean, parameters.response));
    }

    @Test
    public void test_MultiMapContainsEntryCodec_encodeRequest() {
        int fileClientMessageIndex = 208;
        ClientMessage encoded = MultiMapContainsEntryCodec.encodeRequest(aString, aData, aData, aLong);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_MultiMapContainsEntryCodec_decodeResponse() {
        int fileClientMessageIndex = 209;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MultiMapContainsEntryCodec.ResponseParameters parameters = MultiMapContainsEntryCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aBoolean, parameters.response));
    }

    @Test
    public void test_MultiMapSizeCodec_encodeRequest() {
        int fileClientMessageIndex = 210;
        ClientMessage encoded = MultiMapSizeCodec.encodeRequest(aString);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_MultiMapSizeCodec_decodeResponse() {
        int fileClientMessageIndex = 211;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MultiMapSizeCodec.ResponseParameters parameters = MultiMapSizeCodec.decodeResponse(fromFile);
        assertTrue(isEqual(anInt, parameters.response));
    }

    @Test
    public void test_MultiMapClearCodec_encodeRequest() {
        int fileClientMessageIndex = 212;
        ClientMessage encoded = MultiMapClearCodec.encodeRequest(aString);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_MultiMapClearCodec_decodeResponse() {
        int fileClientMessageIndex = 213;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MultiMapClearCodec.ResponseParameters parameters = MultiMapClearCodec.decodeResponse(fromFile);
    }

    @Test
    public void test_MultiMapValueCountCodec_encodeRequest() {
        int fileClientMessageIndex = 214;
        ClientMessage encoded = MultiMapValueCountCodec.encodeRequest(aString, aData, aLong);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_MultiMapValueCountCodec_decodeResponse() {
        int fileClientMessageIndex = 215;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MultiMapValueCountCodec.ResponseParameters parameters = MultiMapValueCountCodec.decodeResponse(fromFile);
        assertTrue(isEqual(anInt, parameters.response));
    }

    @Test
    public void test_MultiMapAddEntryListenerToKeyCodec_encodeRequest() {
        int fileClientMessageIndex = 216;
        ClientMessage encoded = MultiMapAddEntryListenerToKeyCodec.encodeRequest(aString, aData, aBoolean, aBoolean);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_MultiMapAddEntryListenerToKeyCodec_decodeResponse() {
        int fileClientMessageIndex = 217;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MultiMapAddEntryListenerToKeyCodec.ResponseParameters parameters = MultiMapAddEntryListenerToKeyCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aUUID, parameters.response));
    }

    private class MultiMapAddEntryListenerToKeyCodecHandler extends MultiMapAddEntryListenerToKeyCodec.AbstractEventHandler {
        @Override
        public void handleEntryEvent(com.hazelcast.nio.serialization.Data key, com.hazelcast.nio.serialization.Data value, com.hazelcast.nio.serialization.Data oldValue, com.hazelcast.nio.serialization.Data mergingValue, int eventType, java.util.UUID uuid, int numberOfAffectedEntries) {
            assertTrue(isEqual(aData, key));
            assertTrue(isEqual(aData, value));
            assertTrue(isEqual(aData, oldValue));
            assertTrue(isEqual(aData, mergingValue));
            assertTrue(isEqual(anInt, eventType));
            assertTrue(isEqual(aUUID, uuid));
            assertTrue(isEqual(anInt, numberOfAffectedEntries));
        }
    }

    @Test
    public void test_MultiMapAddEntryListenerToKeyCodec_handleEntryEvent() {
        int fileClientMessageIndex = 218;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MultiMapAddEntryListenerToKeyCodecHandler handler = new MultiMapAddEntryListenerToKeyCodecHandler();
        handler.handle(fromFile);
    }

    @Test
    public void test_MultiMapAddEntryListenerCodec_encodeRequest() {
        int fileClientMessageIndex = 219;
        ClientMessage encoded = MultiMapAddEntryListenerCodec.encodeRequest(aString, aBoolean, aBoolean);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_MultiMapAddEntryListenerCodec_decodeResponse() {
        int fileClientMessageIndex = 220;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MultiMapAddEntryListenerCodec.ResponseParameters parameters = MultiMapAddEntryListenerCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aUUID, parameters.response));
    }

    private class MultiMapAddEntryListenerCodecHandler extends MultiMapAddEntryListenerCodec.AbstractEventHandler {
        @Override
        public void handleEntryEvent(com.hazelcast.nio.serialization.Data key, com.hazelcast.nio.serialization.Data value, com.hazelcast.nio.serialization.Data oldValue, com.hazelcast.nio.serialization.Data mergingValue, int eventType, java.util.UUID uuid, int numberOfAffectedEntries) {
            assertTrue(isEqual(aData, key));
            assertTrue(isEqual(aData, value));
            assertTrue(isEqual(aData, oldValue));
            assertTrue(isEqual(aData, mergingValue));
            assertTrue(isEqual(anInt, eventType));
            assertTrue(isEqual(aUUID, uuid));
            assertTrue(isEqual(anInt, numberOfAffectedEntries));
        }
    }

    @Test
    public void test_MultiMapAddEntryListenerCodec_handleEntryEvent() {
        int fileClientMessageIndex = 221;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MultiMapAddEntryListenerCodecHandler handler = new MultiMapAddEntryListenerCodecHandler();
        handler.handle(fromFile);
    }

    @Test
    public void test_MultiMapRemoveEntryListenerCodec_encodeRequest() {
        int fileClientMessageIndex = 222;
        ClientMessage encoded = MultiMapRemoveEntryListenerCodec.encodeRequest(aString, aUUID);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_MultiMapRemoveEntryListenerCodec_decodeResponse() {
        int fileClientMessageIndex = 223;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MultiMapRemoveEntryListenerCodec.ResponseParameters parameters = MultiMapRemoveEntryListenerCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aBoolean, parameters.response));
    }

    @Test
    public void test_MultiMapLockCodec_encodeRequest() {
        int fileClientMessageIndex = 224;
        ClientMessage encoded = MultiMapLockCodec.encodeRequest(aString, aData, aLong, aLong, aLong);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_MultiMapLockCodec_decodeResponse() {
        int fileClientMessageIndex = 225;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MultiMapLockCodec.ResponseParameters parameters = MultiMapLockCodec.decodeResponse(fromFile);
    }

    @Test
    public void test_MultiMapTryLockCodec_encodeRequest() {
        int fileClientMessageIndex = 226;
        ClientMessage encoded = MultiMapTryLockCodec.encodeRequest(aString, aData, aLong, aLong, aLong, aLong);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_MultiMapTryLockCodec_decodeResponse() {
        int fileClientMessageIndex = 227;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MultiMapTryLockCodec.ResponseParameters parameters = MultiMapTryLockCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aBoolean, parameters.response));
    }

    @Test
    public void test_MultiMapIsLockedCodec_encodeRequest() {
        int fileClientMessageIndex = 228;
        ClientMessage encoded = MultiMapIsLockedCodec.encodeRequest(aString, aData);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_MultiMapIsLockedCodec_decodeResponse() {
        int fileClientMessageIndex = 229;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MultiMapIsLockedCodec.ResponseParameters parameters = MultiMapIsLockedCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aBoolean, parameters.response));
    }

    @Test
    public void test_MultiMapUnlockCodec_encodeRequest() {
        int fileClientMessageIndex = 230;
        ClientMessage encoded = MultiMapUnlockCodec.encodeRequest(aString, aData, aLong, aLong);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_MultiMapUnlockCodec_decodeResponse() {
        int fileClientMessageIndex = 231;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MultiMapUnlockCodec.ResponseParameters parameters = MultiMapUnlockCodec.decodeResponse(fromFile);
    }

    @Test
    public void test_MultiMapForceUnlockCodec_encodeRequest() {
        int fileClientMessageIndex = 232;
        ClientMessage encoded = MultiMapForceUnlockCodec.encodeRequest(aString, aData, aLong);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_MultiMapForceUnlockCodec_decodeResponse() {
        int fileClientMessageIndex = 233;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MultiMapForceUnlockCodec.ResponseParameters parameters = MultiMapForceUnlockCodec.decodeResponse(fromFile);
    }

    @Test
    public void test_MultiMapRemoveEntryCodec_encodeRequest() {
        int fileClientMessageIndex = 234;
        ClientMessage encoded = MultiMapRemoveEntryCodec.encodeRequest(aString, aData, aData, aLong);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_MultiMapRemoveEntryCodec_decodeResponse() {
        int fileClientMessageIndex = 235;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MultiMapRemoveEntryCodec.ResponseParameters parameters = MultiMapRemoveEntryCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aBoolean, parameters.response));
    }

    @Test
    public void test_MultiMapDeleteCodec_encodeRequest() {
        int fileClientMessageIndex = 236;
        ClientMessage encoded = MultiMapDeleteCodec.encodeRequest(aString, aData, aLong);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_MultiMapDeleteCodec_decodeResponse() {
        int fileClientMessageIndex = 237;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MultiMapDeleteCodec.ResponseParameters parameters = MultiMapDeleteCodec.decodeResponse(fromFile);
    }

    @Test
    public void test_QueueOfferCodec_encodeRequest() {
        int fileClientMessageIndex = 238;
        ClientMessage encoded = QueueOfferCodec.encodeRequest(aString, aData, aLong);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_QueueOfferCodec_decodeResponse() {
        int fileClientMessageIndex = 239;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        QueueOfferCodec.ResponseParameters parameters = QueueOfferCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aBoolean, parameters.response));
    }

    @Test
    public void test_QueuePutCodec_encodeRequest() {
        int fileClientMessageIndex = 240;
        ClientMessage encoded = QueuePutCodec.encodeRequest(aString, aData);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_QueuePutCodec_decodeResponse() {
        int fileClientMessageIndex = 241;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        QueuePutCodec.ResponseParameters parameters = QueuePutCodec.decodeResponse(fromFile);
    }

    @Test
    public void test_QueueSizeCodec_encodeRequest() {
        int fileClientMessageIndex = 242;
        ClientMessage encoded = QueueSizeCodec.encodeRequest(aString);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_QueueSizeCodec_decodeResponse() {
        int fileClientMessageIndex = 243;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        QueueSizeCodec.ResponseParameters parameters = QueueSizeCodec.decodeResponse(fromFile);
        assertTrue(isEqual(anInt, parameters.response));
    }

    @Test
    public void test_QueueRemoveCodec_encodeRequest() {
        int fileClientMessageIndex = 244;
        ClientMessage encoded = QueueRemoveCodec.encodeRequest(aString, aData);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_QueueRemoveCodec_decodeResponse() {
        int fileClientMessageIndex = 245;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        QueueRemoveCodec.ResponseParameters parameters = QueueRemoveCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aBoolean, parameters.response));
    }

    @Test
    public void test_QueuePollCodec_encodeRequest() {
        int fileClientMessageIndex = 246;
        ClientMessage encoded = QueuePollCodec.encodeRequest(aString, aLong);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_QueuePollCodec_decodeResponse() {
        int fileClientMessageIndex = 247;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        QueuePollCodec.ResponseParameters parameters = QueuePollCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aData, parameters.response));
    }

    @Test
    public void test_QueueTakeCodec_encodeRequest() {
        int fileClientMessageIndex = 248;
        ClientMessage encoded = QueueTakeCodec.encodeRequest(aString);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_QueueTakeCodec_decodeResponse() {
        int fileClientMessageIndex = 249;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        QueueTakeCodec.ResponseParameters parameters = QueueTakeCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aData, parameters.response));
    }

    @Test
    public void test_QueuePeekCodec_encodeRequest() {
        int fileClientMessageIndex = 250;
        ClientMessage encoded = QueuePeekCodec.encodeRequest(aString);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_QueuePeekCodec_decodeResponse() {
        int fileClientMessageIndex = 251;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        QueuePeekCodec.ResponseParameters parameters = QueuePeekCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aData, parameters.response));
    }

    @Test
    public void test_QueueIteratorCodec_encodeRequest() {
        int fileClientMessageIndex = 252;
        ClientMessage encoded = QueueIteratorCodec.encodeRequest(aString);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_QueueIteratorCodec_decodeResponse() {
        int fileClientMessageIndex = 253;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        QueueIteratorCodec.ResponseParameters parameters = QueueIteratorCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aListOfData, parameters.response));
    }

    @Test
    public void test_QueueDrainToCodec_encodeRequest() {
        int fileClientMessageIndex = 254;
        ClientMessage encoded = QueueDrainToCodec.encodeRequest(aString);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_QueueDrainToCodec_decodeResponse() {
        int fileClientMessageIndex = 255;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        QueueDrainToCodec.ResponseParameters parameters = QueueDrainToCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aListOfData, parameters.response));
    }

    @Test
    public void test_QueueDrainToMaxSizeCodec_encodeRequest() {
        int fileClientMessageIndex = 256;
        ClientMessage encoded = QueueDrainToMaxSizeCodec.encodeRequest(aString, anInt);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_QueueDrainToMaxSizeCodec_decodeResponse() {
        int fileClientMessageIndex = 257;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        QueueDrainToMaxSizeCodec.ResponseParameters parameters = QueueDrainToMaxSizeCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aListOfData, parameters.response));
    }

    @Test
    public void test_QueueContainsCodec_encodeRequest() {
        int fileClientMessageIndex = 258;
        ClientMessage encoded = QueueContainsCodec.encodeRequest(aString, aData);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_QueueContainsCodec_decodeResponse() {
        int fileClientMessageIndex = 259;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        QueueContainsCodec.ResponseParameters parameters = QueueContainsCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aBoolean, parameters.response));
    }

    @Test
    public void test_QueueContainsAllCodec_encodeRequest() {
        int fileClientMessageIndex = 260;
        ClientMessage encoded = QueueContainsAllCodec.encodeRequest(aString, aListOfData);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_QueueContainsAllCodec_decodeResponse() {
        int fileClientMessageIndex = 261;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        QueueContainsAllCodec.ResponseParameters parameters = QueueContainsAllCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aBoolean, parameters.response));
    }

    @Test
    public void test_QueueCompareAndRemoveAllCodec_encodeRequest() {
        int fileClientMessageIndex = 262;
        ClientMessage encoded = QueueCompareAndRemoveAllCodec.encodeRequest(aString, aListOfData);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_QueueCompareAndRemoveAllCodec_decodeResponse() {
        int fileClientMessageIndex = 263;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        QueueCompareAndRemoveAllCodec.ResponseParameters parameters = QueueCompareAndRemoveAllCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aBoolean, parameters.response));
    }

    @Test
    public void test_QueueCompareAndRetainAllCodec_encodeRequest() {
        int fileClientMessageIndex = 264;
        ClientMessage encoded = QueueCompareAndRetainAllCodec.encodeRequest(aString, aListOfData);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_QueueCompareAndRetainAllCodec_decodeResponse() {
        int fileClientMessageIndex = 265;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        QueueCompareAndRetainAllCodec.ResponseParameters parameters = QueueCompareAndRetainAllCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aBoolean, parameters.response));
    }

    @Test
    public void test_QueueClearCodec_encodeRequest() {
        int fileClientMessageIndex = 266;
        ClientMessage encoded = QueueClearCodec.encodeRequest(aString);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_QueueClearCodec_decodeResponse() {
        int fileClientMessageIndex = 267;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        QueueClearCodec.ResponseParameters parameters = QueueClearCodec.decodeResponse(fromFile);
    }

    @Test
    public void test_QueueAddAllCodec_encodeRequest() {
        int fileClientMessageIndex = 268;
        ClientMessage encoded = QueueAddAllCodec.encodeRequest(aString, aListOfData);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_QueueAddAllCodec_decodeResponse() {
        int fileClientMessageIndex = 269;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        QueueAddAllCodec.ResponseParameters parameters = QueueAddAllCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aBoolean, parameters.response));
    }

    @Test
    public void test_QueueAddListenerCodec_encodeRequest() {
        int fileClientMessageIndex = 270;
        ClientMessage encoded = QueueAddListenerCodec.encodeRequest(aString, aBoolean, aBoolean);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_QueueAddListenerCodec_decodeResponse() {
        int fileClientMessageIndex = 271;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        QueueAddListenerCodec.ResponseParameters parameters = QueueAddListenerCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aUUID, parameters.response));
    }

    private class QueueAddListenerCodecHandler extends QueueAddListenerCodec.AbstractEventHandler {
        @Override
        public void handleItemEvent(com.hazelcast.nio.serialization.Data item, java.util.UUID uuid, int eventType) {
            assertTrue(isEqual(aData, item));
            assertTrue(isEqual(aUUID, uuid));
            assertTrue(isEqual(anInt, eventType));
        }
    }

    @Test
    public void test_QueueAddListenerCodec_handleItemEvent() {
        int fileClientMessageIndex = 272;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        QueueAddListenerCodecHandler handler = new QueueAddListenerCodecHandler();
        handler.handle(fromFile);
    }

    @Test
    public void test_QueueRemoveListenerCodec_encodeRequest() {
        int fileClientMessageIndex = 273;
        ClientMessage encoded = QueueRemoveListenerCodec.encodeRequest(aString, aUUID);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_QueueRemoveListenerCodec_decodeResponse() {
        int fileClientMessageIndex = 274;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        QueueRemoveListenerCodec.ResponseParameters parameters = QueueRemoveListenerCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aBoolean, parameters.response));
    }

    @Test
    public void test_QueueRemainingCapacityCodec_encodeRequest() {
        int fileClientMessageIndex = 275;
        ClientMessage encoded = QueueRemainingCapacityCodec.encodeRequest(aString);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_QueueRemainingCapacityCodec_decodeResponse() {
        int fileClientMessageIndex = 276;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        QueueRemainingCapacityCodec.ResponseParameters parameters = QueueRemainingCapacityCodec.decodeResponse(fromFile);
        assertTrue(isEqual(anInt, parameters.response));
    }

    @Test
    public void test_QueueIsEmptyCodec_encodeRequest() {
        int fileClientMessageIndex = 277;
        ClientMessage encoded = QueueIsEmptyCodec.encodeRequest(aString);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_QueueIsEmptyCodec_decodeResponse() {
        int fileClientMessageIndex = 278;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        QueueIsEmptyCodec.ResponseParameters parameters = QueueIsEmptyCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aBoolean, parameters.response));
    }

    @Test
    public void test_TopicPublishCodec_encodeRequest() {
        int fileClientMessageIndex = 279;
        ClientMessage encoded = TopicPublishCodec.encodeRequest(aString, aData);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_TopicPublishCodec_decodeResponse() {
        int fileClientMessageIndex = 280;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        TopicPublishCodec.ResponseParameters parameters = TopicPublishCodec.decodeResponse(fromFile);
    }

    @Test
    public void test_TopicAddMessageListenerCodec_encodeRequest() {
        int fileClientMessageIndex = 281;
        ClientMessage encoded = TopicAddMessageListenerCodec.encodeRequest(aString, aBoolean);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_TopicAddMessageListenerCodec_decodeResponse() {
        int fileClientMessageIndex = 282;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        TopicAddMessageListenerCodec.ResponseParameters parameters = TopicAddMessageListenerCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aUUID, parameters.response));
    }

    private class TopicAddMessageListenerCodecHandler extends TopicAddMessageListenerCodec.AbstractEventHandler {
        @Override
        public void handleTopicEvent(com.hazelcast.nio.serialization.Data item, long publishTime, java.util.UUID uuid) {
            assertTrue(isEqual(aData, item));
            assertTrue(isEqual(aLong, publishTime));
            assertTrue(isEqual(aUUID, uuid));
        }
    }

    @Test
    public void test_TopicAddMessageListenerCodec_handleTopicEvent() {
        int fileClientMessageIndex = 283;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        TopicAddMessageListenerCodecHandler handler = new TopicAddMessageListenerCodecHandler();
        handler.handle(fromFile);
    }

    @Test
    public void test_TopicRemoveMessageListenerCodec_encodeRequest() {
        int fileClientMessageIndex = 284;
        ClientMessage encoded = TopicRemoveMessageListenerCodec.encodeRequest(aString, aUUID);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_TopicRemoveMessageListenerCodec_decodeResponse() {
        int fileClientMessageIndex = 285;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        TopicRemoveMessageListenerCodec.ResponseParameters parameters = TopicRemoveMessageListenerCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aBoolean, parameters.response));
    }

    @Test
    public void test_ListSizeCodec_encodeRequest() {
        int fileClientMessageIndex = 286;
        ClientMessage encoded = ListSizeCodec.encodeRequest(aString);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_ListSizeCodec_decodeResponse() {
        int fileClientMessageIndex = 287;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        ListSizeCodec.ResponseParameters parameters = ListSizeCodec.decodeResponse(fromFile);
        assertTrue(isEqual(anInt, parameters.response));
    }

    @Test
    public void test_ListContainsCodec_encodeRequest() {
        int fileClientMessageIndex = 288;
        ClientMessage encoded = ListContainsCodec.encodeRequest(aString, aData);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_ListContainsCodec_decodeResponse() {
        int fileClientMessageIndex = 289;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        ListContainsCodec.ResponseParameters parameters = ListContainsCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aBoolean, parameters.response));
    }

    @Test
    public void test_ListContainsAllCodec_encodeRequest() {
        int fileClientMessageIndex = 290;
        ClientMessage encoded = ListContainsAllCodec.encodeRequest(aString, aListOfData);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_ListContainsAllCodec_decodeResponse() {
        int fileClientMessageIndex = 291;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        ListContainsAllCodec.ResponseParameters parameters = ListContainsAllCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aBoolean, parameters.response));
    }

    @Test
    public void test_ListAddCodec_encodeRequest() {
        int fileClientMessageIndex = 292;
        ClientMessage encoded = ListAddCodec.encodeRequest(aString, aData);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_ListAddCodec_decodeResponse() {
        int fileClientMessageIndex = 293;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        ListAddCodec.ResponseParameters parameters = ListAddCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aBoolean, parameters.response));
    }

    @Test
    public void test_ListRemoveCodec_encodeRequest() {
        int fileClientMessageIndex = 294;
        ClientMessage encoded = ListRemoveCodec.encodeRequest(aString, aData);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_ListRemoveCodec_decodeResponse() {
        int fileClientMessageIndex = 295;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        ListRemoveCodec.ResponseParameters parameters = ListRemoveCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aBoolean, parameters.response));
    }

    @Test
    public void test_ListAddAllCodec_encodeRequest() {
        int fileClientMessageIndex = 296;
        ClientMessage encoded = ListAddAllCodec.encodeRequest(aString, aListOfData);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_ListAddAllCodec_decodeResponse() {
        int fileClientMessageIndex = 297;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        ListAddAllCodec.ResponseParameters parameters = ListAddAllCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aBoolean, parameters.response));
    }

    @Test
    public void test_ListCompareAndRemoveAllCodec_encodeRequest() {
        int fileClientMessageIndex = 298;
        ClientMessage encoded = ListCompareAndRemoveAllCodec.encodeRequest(aString, aListOfData);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_ListCompareAndRemoveAllCodec_decodeResponse() {
        int fileClientMessageIndex = 299;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        ListCompareAndRemoveAllCodec.ResponseParameters parameters = ListCompareAndRemoveAllCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aBoolean, parameters.response));
    }

    @Test
    public void test_ListCompareAndRetainAllCodec_encodeRequest() {
        int fileClientMessageIndex = 300;
        ClientMessage encoded = ListCompareAndRetainAllCodec.encodeRequest(aString, aListOfData);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_ListCompareAndRetainAllCodec_decodeResponse() {
        int fileClientMessageIndex = 301;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        ListCompareAndRetainAllCodec.ResponseParameters parameters = ListCompareAndRetainAllCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aBoolean, parameters.response));
    }

    @Test
    public void test_ListClearCodec_encodeRequest() {
        int fileClientMessageIndex = 302;
        ClientMessage encoded = ListClearCodec.encodeRequest(aString);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_ListClearCodec_decodeResponse() {
        int fileClientMessageIndex = 303;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        ListClearCodec.ResponseParameters parameters = ListClearCodec.decodeResponse(fromFile);
    }

    @Test
    public void test_ListGetAllCodec_encodeRequest() {
        int fileClientMessageIndex = 304;
        ClientMessage encoded = ListGetAllCodec.encodeRequest(aString);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_ListGetAllCodec_decodeResponse() {
        int fileClientMessageIndex = 305;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        ListGetAllCodec.ResponseParameters parameters = ListGetAllCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aListOfData, parameters.response));
    }

    @Test
    public void test_ListAddListenerCodec_encodeRequest() {
        int fileClientMessageIndex = 306;
        ClientMessage encoded = ListAddListenerCodec.encodeRequest(aString, aBoolean, aBoolean);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_ListAddListenerCodec_decodeResponse() {
        int fileClientMessageIndex = 307;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        ListAddListenerCodec.ResponseParameters parameters = ListAddListenerCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aUUID, parameters.response));
    }

    private class ListAddListenerCodecHandler extends ListAddListenerCodec.AbstractEventHandler {
        @Override
        public void handleItemEvent(com.hazelcast.nio.serialization.Data item, java.util.UUID uuid, int eventType) {
            assertTrue(isEqual(aData, item));
            assertTrue(isEqual(aUUID, uuid));
            assertTrue(isEqual(anInt, eventType));
        }
    }

    @Test
    public void test_ListAddListenerCodec_handleItemEvent() {
        int fileClientMessageIndex = 308;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        ListAddListenerCodecHandler handler = new ListAddListenerCodecHandler();
        handler.handle(fromFile);
    }

    @Test
    public void test_ListRemoveListenerCodec_encodeRequest() {
        int fileClientMessageIndex = 309;
        ClientMessage encoded = ListRemoveListenerCodec.encodeRequest(aString, aUUID);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_ListRemoveListenerCodec_decodeResponse() {
        int fileClientMessageIndex = 310;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        ListRemoveListenerCodec.ResponseParameters parameters = ListRemoveListenerCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aBoolean, parameters.response));
    }

    @Test
    public void test_ListIsEmptyCodec_encodeRequest() {
        int fileClientMessageIndex = 311;
        ClientMessage encoded = ListIsEmptyCodec.encodeRequest(aString);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_ListIsEmptyCodec_decodeResponse() {
        int fileClientMessageIndex = 312;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        ListIsEmptyCodec.ResponseParameters parameters = ListIsEmptyCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aBoolean, parameters.response));
    }

    @Test
    public void test_ListAddAllWithIndexCodec_encodeRequest() {
        int fileClientMessageIndex = 313;
        ClientMessage encoded = ListAddAllWithIndexCodec.encodeRequest(aString, anInt, aListOfData);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_ListAddAllWithIndexCodec_decodeResponse() {
        int fileClientMessageIndex = 314;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        ListAddAllWithIndexCodec.ResponseParameters parameters = ListAddAllWithIndexCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aBoolean, parameters.response));
    }

    @Test
    public void test_ListGetCodec_encodeRequest() {
        int fileClientMessageIndex = 315;
        ClientMessage encoded = ListGetCodec.encodeRequest(aString, anInt);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_ListGetCodec_decodeResponse() {
        int fileClientMessageIndex = 316;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        ListGetCodec.ResponseParameters parameters = ListGetCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aData, parameters.response));
    }

    @Test
    public void test_ListSetCodec_encodeRequest() {
        int fileClientMessageIndex = 317;
        ClientMessage encoded = ListSetCodec.encodeRequest(aString, anInt, aData);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_ListSetCodec_decodeResponse() {
        int fileClientMessageIndex = 318;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        ListSetCodec.ResponseParameters parameters = ListSetCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aData, parameters.response));
    }

    @Test
    public void test_ListAddWithIndexCodec_encodeRequest() {
        int fileClientMessageIndex = 319;
        ClientMessage encoded = ListAddWithIndexCodec.encodeRequest(aString, anInt, aData);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_ListAddWithIndexCodec_decodeResponse() {
        int fileClientMessageIndex = 320;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        ListAddWithIndexCodec.ResponseParameters parameters = ListAddWithIndexCodec.decodeResponse(fromFile);
    }

    @Test
    public void test_ListRemoveWithIndexCodec_encodeRequest() {
        int fileClientMessageIndex = 321;
        ClientMessage encoded = ListRemoveWithIndexCodec.encodeRequest(aString, anInt);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_ListRemoveWithIndexCodec_decodeResponse() {
        int fileClientMessageIndex = 322;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        ListRemoveWithIndexCodec.ResponseParameters parameters = ListRemoveWithIndexCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aData, parameters.response));
    }

    @Test
    public void test_ListLastIndexOfCodec_encodeRequest() {
        int fileClientMessageIndex = 323;
        ClientMessage encoded = ListLastIndexOfCodec.encodeRequest(aString, aData);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_ListLastIndexOfCodec_decodeResponse() {
        int fileClientMessageIndex = 324;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        ListLastIndexOfCodec.ResponseParameters parameters = ListLastIndexOfCodec.decodeResponse(fromFile);
        assertTrue(isEqual(anInt, parameters.response));
    }

    @Test
    public void test_ListIndexOfCodec_encodeRequest() {
        int fileClientMessageIndex = 325;
        ClientMessage encoded = ListIndexOfCodec.encodeRequest(aString, aData);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_ListIndexOfCodec_decodeResponse() {
        int fileClientMessageIndex = 326;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        ListIndexOfCodec.ResponseParameters parameters = ListIndexOfCodec.decodeResponse(fromFile);
        assertTrue(isEqual(anInt, parameters.response));
    }

    @Test
    public void test_ListSubCodec_encodeRequest() {
        int fileClientMessageIndex = 327;
        ClientMessage encoded = ListSubCodec.encodeRequest(aString, anInt, anInt);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_ListSubCodec_decodeResponse() {
        int fileClientMessageIndex = 328;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        ListSubCodec.ResponseParameters parameters = ListSubCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aListOfData, parameters.response));
    }

    @Test
    public void test_ListIteratorCodec_encodeRequest() {
        int fileClientMessageIndex = 329;
        ClientMessage encoded = ListIteratorCodec.encodeRequest(aString);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_ListIteratorCodec_decodeResponse() {
        int fileClientMessageIndex = 330;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        ListIteratorCodec.ResponseParameters parameters = ListIteratorCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aListOfData, parameters.response));
    }

    @Test
    public void test_ListListIteratorCodec_encodeRequest() {
        int fileClientMessageIndex = 331;
        ClientMessage encoded = ListListIteratorCodec.encodeRequest(aString, anInt);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_ListListIteratorCodec_decodeResponse() {
        int fileClientMessageIndex = 332;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        ListListIteratorCodec.ResponseParameters parameters = ListListIteratorCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aListOfData, parameters.response));
    }

    @Test
    public void test_SetSizeCodec_encodeRequest() {
        int fileClientMessageIndex = 333;
        ClientMessage encoded = SetSizeCodec.encodeRequest(aString);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_SetSizeCodec_decodeResponse() {
        int fileClientMessageIndex = 334;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        SetSizeCodec.ResponseParameters parameters = SetSizeCodec.decodeResponse(fromFile);
        assertTrue(isEqual(anInt, parameters.response));
    }

    @Test
    public void test_SetContainsCodec_encodeRequest() {
        int fileClientMessageIndex = 335;
        ClientMessage encoded = SetContainsCodec.encodeRequest(aString, aData);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_SetContainsCodec_decodeResponse() {
        int fileClientMessageIndex = 336;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        SetContainsCodec.ResponseParameters parameters = SetContainsCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aBoolean, parameters.response));
    }

    @Test
    public void test_SetContainsAllCodec_encodeRequest() {
        int fileClientMessageIndex = 337;
        ClientMessage encoded = SetContainsAllCodec.encodeRequest(aString, aListOfData);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_SetContainsAllCodec_decodeResponse() {
        int fileClientMessageIndex = 338;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        SetContainsAllCodec.ResponseParameters parameters = SetContainsAllCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aBoolean, parameters.response));
    }

    @Test
    public void test_SetAddCodec_encodeRequest() {
        int fileClientMessageIndex = 339;
        ClientMessage encoded = SetAddCodec.encodeRequest(aString, aData);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_SetAddCodec_decodeResponse() {
        int fileClientMessageIndex = 340;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        SetAddCodec.ResponseParameters parameters = SetAddCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aBoolean, parameters.response));
    }

    @Test
    public void test_SetRemoveCodec_encodeRequest() {
        int fileClientMessageIndex = 341;
        ClientMessage encoded = SetRemoveCodec.encodeRequest(aString, aData);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_SetRemoveCodec_decodeResponse() {
        int fileClientMessageIndex = 342;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        SetRemoveCodec.ResponseParameters parameters = SetRemoveCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aBoolean, parameters.response));
    }

    @Test
    public void test_SetAddAllCodec_encodeRequest() {
        int fileClientMessageIndex = 343;
        ClientMessage encoded = SetAddAllCodec.encodeRequest(aString, aListOfData);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_SetAddAllCodec_decodeResponse() {
        int fileClientMessageIndex = 344;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        SetAddAllCodec.ResponseParameters parameters = SetAddAllCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aBoolean, parameters.response));
    }

    @Test
    public void test_SetCompareAndRemoveAllCodec_encodeRequest() {
        int fileClientMessageIndex = 345;
        ClientMessage encoded = SetCompareAndRemoveAllCodec.encodeRequest(aString, aListOfData);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_SetCompareAndRemoveAllCodec_decodeResponse() {
        int fileClientMessageIndex = 346;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        SetCompareAndRemoveAllCodec.ResponseParameters parameters = SetCompareAndRemoveAllCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aBoolean, parameters.response));
    }

    @Test
    public void test_SetCompareAndRetainAllCodec_encodeRequest() {
        int fileClientMessageIndex = 347;
        ClientMessage encoded = SetCompareAndRetainAllCodec.encodeRequest(aString, aListOfData);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_SetCompareAndRetainAllCodec_decodeResponse() {
        int fileClientMessageIndex = 348;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        SetCompareAndRetainAllCodec.ResponseParameters parameters = SetCompareAndRetainAllCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aBoolean, parameters.response));
    }

    @Test
    public void test_SetClearCodec_encodeRequest() {
        int fileClientMessageIndex = 349;
        ClientMessage encoded = SetClearCodec.encodeRequest(aString);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_SetClearCodec_decodeResponse() {
        int fileClientMessageIndex = 350;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        SetClearCodec.ResponseParameters parameters = SetClearCodec.decodeResponse(fromFile);
    }

    @Test
    public void test_SetGetAllCodec_encodeRequest() {
        int fileClientMessageIndex = 351;
        ClientMessage encoded = SetGetAllCodec.encodeRequest(aString);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_SetGetAllCodec_decodeResponse() {
        int fileClientMessageIndex = 352;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        SetGetAllCodec.ResponseParameters parameters = SetGetAllCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aListOfData, parameters.response));
    }

    @Test
    public void test_SetAddListenerCodec_encodeRequest() {
        int fileClientMessageIndex = 353;
        ClientMessage encoded = SetAddListenerCodec.encodeRequest(aString, aBoolean, aBoolean);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_SetAddListenerCodec_decodeResponse() {
        int fileClientMessageIndex = 354;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        SetAddListenerCodec.ResponseParameters parameters = SetAddListenerCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aUUID, parameters.response));
    }

    private class SetAddListenerCodecHandler extends SetAddListenerCodec.AbstractEventHandler {
        @Override
        public void handleItemEvent(com.hazelcast.nio.serialization.Data item, java.util.UUID uuid, int eventType) {
            assertTrue(isEqual(aData, item));
            assertTrue(isEqual(aUUID, uuid));
            assertTrue(isEqual(anInt, eventType));
        }
    }

    @Test
    public void test_SetAddListenerCodec_handleItemEvent() {
        int fileClientMessageIndex = 355;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        SetAddListenerCodecHandler handler = new SetAddListenerCodecHandler();
        handler.handle(fromFile);
    }

    @Test
    public void test_SetRemoveListenerCodec_encodeRequest() {
        int fileClientMessageIndex = 356;
        ClientMessage encoded = SetRemoveListenerCodec.encodeRequest(aString, aUUID);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_SetRemoveListenerCodec_decodeResponse() {
        int fileClientMessageIndex = 357;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        SetRemoveListenerCodec.ResponseParameters parameters = SetRemoveListenerCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aBoolean, parameters.response));
    }

    @Test
    public void test_SetIsEmptyCodec_encodeRequest() {
        int fileClientMessageIndex = 358;
        ClientMessage encoded = SetIsEmptyCodec.encodeRequest(aString);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_SetIsEmptyCodec_decodeResponse() {
        int fileClientMessageIndex = 359;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        SetIsEmptyCodec.ResponseParameters parameters = SetIsEmptyCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aBoolean, parameters.response));
    }

    @Test
    public void test_FencedLockLockCodec_encodeRequest() {
        int fileClientMessageIndex = 360;
        ClientMessage encoded = FencedLockLockCodec.encodeRequest(aRaftGroupId, aString, aLong, aLong, aUUID);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_FencedLockLockCodec_decodeResponse() {
        int fileClientMessageIndex = 361;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        FencedLockLockCodec.ResponseParameters parameters = FencedLockLockCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aLong, parameters.response));
    }

    @Test
    public void test_FencedLockTryLockCodec_encodeRequest() {
        int fileClientMessageIndex = 362;
        ClientMessage encoded = FencedLockTryLockCodec.encodeRequest(aRaftGroupId, aString, aLong, aLong, aUUID, aLong);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_FencedLockTryLockCodec_decodeResponse() {
        int fileClientMessageIndex = 363;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        FencedLockTryLockCodec.ResponseParameters parameters = FencedLockTryLockCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aLong, parameters.response));
    }

    @Test
    public void test_FencedLockUnlockCodec_encodeRequest() {
        int fileClientMessageIndex = 364;
        ClientMessage encoded = FencedLockUnlockCodec.encodeRequest(aRaftGroupId, aString, aLong, aLong, aUUID);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_FencedLockUnlockCodec_decodeResponse() {
        int fileClientMessageIndex = 365;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        FencedLockUnlockCodec.ResponseParameters parameters = FencedLockUnlockCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aBoolean, parameters.response));
    }

    @Test
    public void test_FencedLockGetLockOwnershipCodec_encodeRequest() {
        int fileClientMessageIndex = 366;
        ClientMessage encoded = FencedLockGetLockOwnershipCodec.encodeRequest(aRaftGroupId, aString);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_FencedLockGetLockOwnershipCodec_decodeResponse() {
        int fileClientMessageIndex = 367;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        FencedLockGetLockOwnershipCodec.ResponseParameters parameters = FencedLockGetLockOwnershipCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aLong, parameters.fence));
        assertTrue(isEqual(anInt, parameters.lockCount));
        assertTrue(isEqual(aLong, parameters.sessionId));
        assertTrue(isEqual(aLong, parameters.threadId));
    }

    @Test
    public void test_ExecutorServiceShutdownCodec_encodeRequest() {
        int fileClientMessageIndex = 368;
        ClientMessage encoded = ExecutorServiceShutdownCodec.encodeRequest(aString);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_ExecutorServiceShutdownCodec_decodeResponse() {
        int fileClientMessageIndex = 369;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        ExecutorServiceShutdownCodec.ResponseParameters parameters = ExecutorServiceShutdownCodec.decodeResponse(fromFile);
    }

    @Test
    public void test_ExecutorServiceIsShutdownCodec_encodeRequest() {
        int fileClientMessageIndex = 370;
        ClientMessage encoded = ExecutorServiceIsShutdownCodec.encodeRequest(aString);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_ExecutorServiceIsShutdownCodec_decodeResponse() {
        int fileClientMessageIndex = 371;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        ExecutorServiceIsShutdownCodec.ResponseParameters parameters = ExecutorServiceIsShutdownCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aBoolean, parameters.response));
    }

    @Test
    public void test_ExecutorServiceCancelOnPartitionCodec_encodeRequest() {
        int fileClientMessageIndex = 372;
        ClientMessage encoded = ExecutorServiceCancelOnPartitionCodec.encodeRequest(aUUID, aBoolean);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_ExecutorServiceCancelOnPartitionCodec_decodeResponse() {
        int fileClientMessageIndex = 373;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        ExecutorServiceCancelOnPartitionCodec.ResponseParameters parameters = ExecutorServiceCancelOnPartitionCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aBoolean, parameters.response));
    }

    @Test
    public void test_ExecutorServiceCancelOnAddressCodec_encodeRequest() {
        int fileClientMessageIndex = 374;
        ClientMessage encoded = ExecutorServiceCancelOnAddressCodec.encodeRequest(aUUID, anAddress, aBoolean);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_ExecutorServiceCancelOnAddressCodec_decodeResponse() {
        int fileClientMessageIndex = 375;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        ExecutorServiceCancelOnAddressCodec.ResponseParameters parameters = ExecutorServiceCancelOnAddressCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aBoolean, parameters.response));
    }

    @Test
    public void test_ExecutorServiceSubmitToPartitionCodec_encodeRequest() {
        int fileClientMessageIndex = 376;
        ClientMessage encoded = ExecutorServiceSubmitToPartitionCodec.encodeRequest(aString, aUUID, aData);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_ExecutorServiceSubmitToPartitionCodec_decodeResponse() {
        int fileClientMessageIndex = 377;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        ExecutorServiceSubmitToPartitionCodec.ResponseParameters parameters = ExecutorServiceSubmitToPartitionCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aData, parameters.response));
    }

    @Test
    public void test_ExecutorServiceSubmitToAddressCodec_encodeRequest() {
        int fileClientMessageIndex = 378;
        ClientMessage encoded = ExecutorServiceSubmitToAddressCodec.encodeRequest(aString, aUUID, aData, anAddress);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_ExecutorServiceSubmitToAddressCodec_decodeResponse() {
        int fileClientMessageIndex = 379;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        ExecutorServiceSubmitToAddressCodec.ResponseParameters parameters = ExecutorServiceSubmitToAddressCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aData, parameters.response));
    }

    @Test
    public void test_AtomicLongApplyCodec_encodeRequest() {
        int fileClientMessageIndex = 380;
        ClientMessage encoded = AtomicLongApplyCodec.encodeRequest(aRaftGroupId, aString, aData);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_AtomicLongApplyCodec_decodeResponse() {
        int fileClientMessageIndex = 381;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        AtomicLongApplyCodec.ResponseParameters parameters = AtomicLongApplyCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aData, parameters.response));
    }

    @Test
    public void test_AtomicLongAlterCodec_encodeRequest() {
        int fileClientMessageIndex = 382;
        ClientMessage encoded = AtomicLongAlterCodec.encodeRequest(aRaftGroupId, aString, aData, anInt);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_AtomicLongAlterCodec_decodeResponse() {
        int fileClientMessageIndex = 383;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        AtomicLongAlterCodec.ResponseParameters parameters = AtomicLongAlterCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aLong, parameters.response));
    }

    @Test
    public void test_AtomicLongAddAndGetCodec_encodeRequest() {
        int fileClientMessageIndex = 384;
        ClientMessage encoded = AtomicLongAddAndGetCodec.encodeRequest(aRaftGroupId, aString, aLong);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_AtomicLongAddAndGetCodec_decodeResponse() {
        int fileClientMessageIndex = 385;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        AtomicLongAddAndGetCodec.ResponseParameters parameters = AtomicLongAddAndGetCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aLong, parameters.response));
    }

    @Test
    public void test_AtomicLongCompareAndSetCodec_encodeRequest() {
        int fileClientMessageIndex = 386;
        ClientMessage encoded = AtomicLongCompareAndSetCodec.encodeRequest(aRaftGroupId, aString, aLong, aLong);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_AtomicLongCompareAndSetCodec_decodeResponse() {
        int fileClientMessageIndex = 387;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        AtomicLongCompareAndSetCodec.ResponseParameters parameters = AtomicLongCompareAndSetCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aBoolean, parameters.response));
    }

    @Test
    public void test_AtomicLongGetCodec_encodeRequest() {
        int fileClientMessageIndex = 388;
        ClientMessage encoded = AtomicLongGetCodec.encodeRequest(aRaftGroupId, aString);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_AtomicLongGetCodec_decodeResponse() {
        int fileClientMessageIndex = 389;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        AtomicLongGetCodec.ResponseParameters parameters = AtomicLongGetCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aLong, parameters.response));
    }

    @Test
    public void test_AtomicLongGetAndAddCodec_encodeRequest() {
        int fileClientMessageIndex = 390;
        ClientMessage encoded = AtomicLongGetAndAddCodec.encodeRequest(aRaftGroupId, aString, aLong);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_AtomicLongGetAndAddCodec_decodeResponse() {
        int fileClientMessageIndex = 391;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        AtomicLongGetAndAddCodec.ResponseParameters parameters = AtomicLongGetAndAddCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aLong, parameters.response));
    }

    @Test
    public void test_AtomicLongGetAndSetCodec_encodeRequest() {
        int fileClientMessageIndex = 392;
        ClientMessage encoded = AtomicLongGetAndSetCodec.encodeRequest(aRaftGroupId, aString, aLong);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_AtomicLongGetAndSetCodec_decodeResponse() {
        int fileClientMessageIndex = 393;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        AtomicLongGetAndSetCodec.ResponseParameters parameters = AtomicLongGetAndSetCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aLong, parameters.response));
    }

    @Test
    public void test_AtomicRefApplyCodec_encodeRequest() {
        int fileClientMessageIndex = 394;
        ClientMessage encoded = AtomicRefApplyCodec.encodeRequest(aRaftGroupId, aString, aData, anInt, aBoolean);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_AtomicRefApplyCodec_decodeResponse() {
        int fileClientMessageIndex = 395;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        AtomicRefApplyCodec.ResponseParameters parameters = AtomicRefApplyCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aData, parameters.response));
    }

    @Test
    public void test_AtomicRefCompareAndSetCodec_encodeRequest() {
        int fileClientMessageIndex = 396;
        ClientMessage encoded = AtomicRefCompareAndSetCodec.encodeRequest(aRaftGroupId, aString, aData, aData);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_AtomicRefCompareAndSetCodec_decodeResponse() {
        int fileClientMessageIndex = 397;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        AtomicRefCompareAndSetCodec.ResponseParameters parameters = AtomicRefCompareAndSetCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aBoolean, parameters.response));
    }

    @Test
    public void test_AtomicRefContainsCodec_encodeRequest() {
        int fileClientMessageIndex = 398;
        ClientMessage encoded = AtomicRefContainsCodec.encodeRequest(aRaftGroupId, aString, aData);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_AtomicRefContainsCodec_decodeResponse() {
        int fileClientMessageIndex = 399;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        AtomicRefContainsCodec.ResponseParameters parameters = AtomicRefContainsCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aBoolean, parameters.response));
    }

    @Test
    public void test_AtomicRefGetCodec_encodeRequest() {
        int fileClientMessageIndex = 400;
        ClientMessage encoded = AtomicRefGetCodec.encodeRequest(aRaftGroupId, aString);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_AtomicRefGetCodec_decodeResponse() {
        int fileClientMessageIndex = 401;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        AtomicRefGetCodec.ResponseParameters parameters = AtomicRefGetCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aData, parameters.response));
    }

    @Test
    public void test_AtomicRefSetCodec_encodeRequest() {
        int fileClientMessageIndex = 402;
        ClientMessage encoded = AtomicRefSetCodec.encodeRequest(aRaftGroupId, aString, aData, aBoolean);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_AtomicRefSetCodec_decodeResponse() {
        int fileClientMessageIndex = 403;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        AtomicRefSetCodec.ResponseParameters parameters = AtomicRefSetCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aData, parameters.response));
    }

    @Test
    public void test_CountDownLatchTrySetCountCodec_encodeRequest() {
        int fileClientMessageIndex = 404;
        ClientMessage encoded = CountDownLatchTrySetCountCodec.encodeRequest(aRaftGroupId, aString, anInt);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_CountDownLatchTrySetCountCodec_decodeResponse() {
        int fileClientMessageIndex = 405;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        CountDownLatchTrySetCountCodec.ResponseParameters parameters = CountDownLatchTrySetCountCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aBoolean, parameters.response));
    }

    @Test
    public void test_CountDownLatchAwaitCodec_encodeRequest() {
        int fileClientMessageIndex = 406;
        ClientMessage encoded = CountDownLatchAwaitCodec.encodeRequest(aRaftGroupId, aString, aUUID, aLong);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_CountDownLatchAwaitCodec_decodeResponse() {
        int fileClientMessageIndex = 407;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        CountDownLatchAwaitCodec.ResponseParameters parameters = CountDownLatchAwaitCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aBoolean, parameters.response));
    }

    @Test
    public void test_CountDownLatchCountDownCodec_encodeRequest() {
        int fileClientMessageIndex = 408;
        ClientMessage encoded = CountDownLatchCountDownCodec.encodeRequest(aRaftGroupId, aString, aUUID, anInt);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_CountDownLatchCountDownCodec_decodeResponse() {
        int fileClientMessageIndex = 409;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        CountDownLatchCountDownCodec.ResponseParameters parameters = CountDownLatchCountDownCodec.decodeResponse(fromFile);
    }

    @Test
    public void test_CountDownLatchGetCountCodec_encodeRequest() {
        int fileClientMessageIndex = 410;
        ClientMessage encoded = CountDownLatchGetCountCodec.encodeRequest(aRaftGroupId, aString);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_CountDownLatchGetCountCodec_decodeResponse() {
        int fileClientMessageIndex = 411;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        CountDownLatchGetCountCodec.ResponseParameters parameters = CountDownLatchGetCountCodec.decodeResponse(fromFile);
        assertTrue(isEqual(anInt, parameters.response));
    }

    @Test
    public void test_CountDownLatchGetRoundCodec_encodeRequest() {
        int fileClientMessageIndex = 412;
        ClientMessage encoded = CountDownLatchGetRoundCodec.encodeRequest(aRaftGroupId, aString);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_CountDownLatchGetRoundCodec_decodeResponse() {
        int fileClientMessageIndex = 413;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        CountDownLatchGetRoundCodec.ResponseParameters parameters = CountDownLatchGetRoundCodec.decodeResponse(fromFile);
        assertTrue(isEqual(anInt, parameters.response));
    }

    @Test
    public void test_SemaphoreInitCodec_encodeRequest() {
        int fileClientMessageIndex = 414;
        ClientMessage encoded = SemaphoreInitCodec.encodeRequest(aRaftGroupId, aString, anInt);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_SemaphoreInitCodec_decodeResponse() {
        int fileClientMessageIndex = 415;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        SemaphoreInitCodec.ResponseParameters parameters = SemaphoreInitCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aBoolean, parameters.response));
    }

    @Test
    public void test_SemaphoreAcquireCodec_encodeRequest() {
        int fileClientMessageIndex = 416;
        ClientMessage encoded = SemaphoreAcquireCodec.encodeRequest(aRaftGroupId, aString, aLong, aLong, aUUID, anInt, aLong);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_SemaphoreAcquireCodec_decodeResponse() {
        int fileClientMessageIndex = 417;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        SemaphoreAcquireCodec.ResponseParameters parameters = SemaphoreAcquireCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aBoolean, parameters.response));
    }

    @Test
    public void test_SemaphoreReleaseCodec_encodeRequest() {
        int fileClientMessageIndex = 418;
        ClientMessage encoded = SemaphoreReleaseCodec.encodeRequest(aRaftGroupId, aString, aLong, aLong, aUUID, anInt);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_SemaphoreReleaseCodec_decodeResponse() {
        int fileClientMessageIndex = 419;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        SemaphoreReleaseCodec.ResponseParameters parameters = SemaphoreReleaseCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aBoolean, parameters.response));
    }

    @Test
    public void test_SemaphoreDrainCodec_encodeRequest() {
        int fileClientMessageIndex = 420;
        ClientMessage encoded = SemaphoreDrainCodec.encodeRequest(aRaftGroupId, aString, aLong, aLong, aUUID);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_SemaphoreDrainCodec_decodeResponse() {
        int fileClientMessageIndex = 421;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        SemaphoreDrainCodec.ResponseParameters parameters = SemaphoreDrainCodec.decodeResponse(fromFile);
        assertTrue(isEqual(anInt, parameters.response));
    }

    @Test
    public void test_SemaphoreChangeCodec_encodeRequest() {
        int fileClientMessageIndex = 422;
        ClientMessage encoded = SemaphoreChangeCodec.encodeRequest(aRaftGroupId, aString, aLong, aLong, aUUID, anInt);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_SemaphoreChangeCodec_decodeResponse() {
        int fileClientMessageIndex = 423;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        SemaphoreChangeCodec.ResponseParameters parameters = SemaphoreChangeCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aBoolean, parameters.response));
    }

    @Test
    public void test_SemaphoreAvailablePermitsCodec_encodeRequest() {
        int fileClientMessageIndex = 424;
        ClientMessage encoded = SemaphoreAvailablePermitsCodec.encodeRequest(aRaftGroupId, aString);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_SemaphoreAvailablePermitsCodec_decodeResponse() {
        int fileClientMessageIndex = 425;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        SemaphoreAvailablePermitsCodec.ResponseParameters parameters = SemaphoreAvailablePermitsCodec.decodeResponse(fromFile);
        assertTrue(isEqual(anInt, parameters.response));
    }

    @Test
    public void test_SemaphoreGetSemaphoreTypeCodec_encodeRequest() {
        int fileClientMessageIndex = 426;
        ClientMessage encoded = SemaphoreGetSemaphoreTypeCodec.encodeRequest(aString);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_SemaphoreGetSemaphoreTypeCodec_decodeResponse() {
        int fileClientMessageIndex = 427;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        SemaphoreGetSemaphoreTypeCodec.ResponseParameters parameters = SemaphoreGetSemaphoreTypeCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aBoolean, parameters.response));
    }

    @Test
    public void test_ReplicatedMapPutCodec_encodeRequest() {
        int fileClientMessageIndex = 428;
        ClientMessage encoded = ReplicatedMapPutCodec.encodeRequest(aString, aData, aData, aLong);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_ReplicatedMapPutCodec_decodeResponse() {
        int fileClientMessageIndex = 429;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        ReplicatedMapPutCodec.ResponseParameters parameters = ReplicatedMapPutCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aData, parameters.response));
    }

    @Test
    public void test_ReplicatedMapSizeCodec_encodeRequest() {
        int fileClientMessageIndex = 430;
        ClientMessage encoded = ReplicatedMapSizeCodec.encodeRequest(aString);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_ReplicatedMapSizeCodec_decodeResponse() {
        int fileClientMessageIndex = 431;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        ReplicatedMapSizeCodec.ResponseParameters parameters = ReplicatedMapSizeCodec.decodeResponse(fromFile);
        assertTrue(isEqual(anInt, parameters.response));
    }

    @Test
    public void test_ReplicatedMapIsEmptyCodec_encodeRequest() {
        int fileClientMessageIndex = 432;
        ClientMessage encoded = ReplicatedMapIsEmptyCodec.encodeRequest(aString);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_ReplicatedMapIsEmptyCodec_decodeResponse() {
        int fileClientMessageIndex = 433;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        ReplicatedMapIsEmptyCodec.ResponseParameters parameters = ReplicatedMapIsEmptyCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aBoolean, parameters.response));
    }

    @Test
    public void test_ReplicatedMapContainsKeyCodec_encodeRequest() {
        int fileClientMessageIndex = 434;
        ClientMessage encoded = ReplicatedMapContainsKeyCodec.encodeRequest(aString, aData);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_ReplicatedMapContainsKeyCodec_decodeResponse() {
        int fileClientMessageIndex = 435;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        ReplicatedMapContainsKeyCodec.ResponseParameters parameters = ReplicatedMapContainsKeyCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aBoolean, parameters.response));
    }

    @Test
    public void test_ReplicatedMapContainsValueCodec_encodeRequest() {
        int fileClientMessageIndex = 436;
        ClientMessage encoded = ReplicatedMapContainsValueCodec.encodeRequest(aString, aData);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_ReplicatedMapContainsValueCodec_decodeResponse() {
        int fileClientMessageIndex = 437;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        ReplicatedMapContainsValueCodec.ResponseParameters parameters = ReplicatedMapContainsValueCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aBoolean, parameters.response));
    }

    @Test
    public void test_ReplicatedMapGetCodec_encodeRequest() {
        int fileClientMessageIndex = 438;
        ClientMessage encoded = ReplicatedMapGetCodec.encodeRequest(aString, aData);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_ReplicatedMapGetCodec_decodeResponse() {
        int fileClientMessageIndex = 439;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        ReplicatedMapGetCodec.ResponseParameters parameters = ReplicatedMapGetCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aData, parameters.response));
    }

    @Test
    public void test_ReplicatedMapRemoveCodec_encodeRequest() {
        int fileClientMessageIndex = 440;
        ClientMessage encoded = ReplicatedMapRemoveCodec.encodeRequest(aString, aData);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_ReplicatedMapRemoveCodec_decodeResponse() {
        int fileClientMessageIndex = 441;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        ReplicatedMapRemoveCodec.ResponseParameters parameters = ReplicatedMapRemoveCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aData, parameters.response));
    }

    @Test
    public void test_ReplicatedMapPutAllCodec_encodeRequest() {
        int fileClientMessageIndex = 442;
        ClientMessage encoded = ReplicatedMapPutAllCodec.encodeRequest(aString, aListOfDataToData);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_ReplicatedMapPutAllCodec_decodeResponse() {
        int fileClientMessageIndex = 443;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        ReplicatedMapPutAllCodec.ResponseParameters parameters = ReplicatedMapPutAllCodec.decodeResponse(fromFile);
    }

    @Test
    public void test_ReplicatedMapClearCodec_encodeRequest() {
        int fileClientMessageIndex = 444;
        ClientMessage encoded = ReplicatedMapClearCodec.encodeRequest(aString);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_ReplicatedMapClearCodec_decodeResponse() {
        int fileClientMessageIndex = 445;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        ReplicatedMapClearCodec.ResponseParameters parameters = ReplicatedMapClearCodec.decodeResponse(fromFile);
    }

    @Test
    public void test_ReplicatedMapAddEntryListenerToKeyWithPredicateCodec_encodeRequest() {
        int fileClientMessageIndex = 446;
        ClientMessage encoded = ReplicatedMapAddEntryListenerToKeyWithPredicateCodec.encodeRequest(aString, aData, aData, aBoolean);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_ReplicatedMapAddEntryListenerToKeyWithPredicateCodec_decodeResponse() {
        int fileClientMessageIndex = 447;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        ReplicatedMapAddEntryListenerToKeyWithPredicateCodec.ResponseParameters parameters = ReplicatedMapAddEntryListenerToKeyWithPredicateCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aUUID, parameters.response));
    }

    private class ReplicatedMapAddEntryListenerToKeyWithPredicateCodecHandler extends ReplicatedMapAddEntryListenerToKeyWithPredicateCodec.AbstractEventHandler {
        @Override
        public void handleEntryEvent(com.hazelcast.nio.serialization.Data key, com.hazelcast.nio.serialization.Data value, com.hazelcast.nio.serialization.Data oldValue, com.hazelcast.nio.serialization.Data mergingValue, int eventType, java.util.UUID uuid, int numberOfAffectedEntries) {
            assertTrue(isEqual(aData, key));
            assertTrue(isEqual(aData, value));
            assertTrue(isEqual(aData, oldValue));
            assertTrue(isEqual(aData, mergingValue));
            assertTrue(isEqual(anInt, eventType));
            assertTrue(isEqual(aUUID, uuid));
            assertTrue(isEqual(anInt, numberOfAffectedEntries));
        }
    }

    @Test
    public void test_ReplicatedMapAddEntryListenerToKeyWithPredicateCodec_handleEntryEvent() {
        int fileClientMessageIndex = 448;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        ReplicatedMapAddEntryListenerToKeyWithPredicateCodecHandler handler = new ReplicatedMapAddEntryListenerToKeyWithPredicateCodecHandler();
        handler.handle(fromFile);
    }

    @Test
    public void test_ReplicatedMapAddEntryListenerWithPredicateCodec_encodeRequest() {
        int fileClientMessageIndex = 449;
        ClientMessage encoded = ReplicatedMapAddEntryListenerWithPredicateCodec.encodeRequest(aString, aData, aBoolean);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_ReplicatedMapAddEntryListenerWithPredicateCodec_decodeResponse() {
        int fileClientMessageIndex = 450;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        ReplicatedMapAddEntryListenerWithPredicateCodec.ResponseParameters parameters = ReplicatedMapAddEntryListenerWithPredicateCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aUUID, parameters.response));
    }

    private class ReplicatedMapAddEntryListenerWithPredicateCodecHandler extends ReplicatedMapAddEntryListenerWithPredicateCodec.AbstractEventHandler {
        @Override
        public void handleEntryEvent(com.hazelcast.nio.serialization.Data key, com.hazelcast.nio.serialization.Data value, com.hazelcast.nio.serialization.Data oldValue, com.hazelcast.nio.serialization.Data mergingValue, int eventType, java.util.UUID uuid, int numberOfAffectedEntries) {
            assertTrue(isEqual(aData, key));
            assertTrue(isEqual(aData, value));
            assertTrue(isEqual(aData, oldValue));
            assertTrue(isEqual(aData, mergingValue));
            assertTrue(isEqual(anInt, eventType));
            assertTrue(isEqual(aUUID, uuid));
            assertTrue(isEqual(anInt, numberOfAffectedEntries));
        }
    }

    @Test
    public void test_ReplicatedMapAddEntryListenerWithPredicateCodec_handleEntryEvent() {
        int fileClientMessageIndex = 451;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        ReplicatedMapAddEntryListenerWithPredicateCodecHandler handler = new ReplicatedMapAddEntryListenerWithPredicateCodecHandler();
        handler.handle(fromFile);
    }

    @Test
    public void test_ReplicatedMapAddEntryListenerToKeyCodec_encodeRequest() {
        int fileClientMessageIndex = 452;
        ClientMessage encoded = ReplicatedMapAddEntryListenerToKeyCodec.encodeRequest(aString, aData, aBoolean);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_ReplicatedMapAddEntryListenerToKeyCodec_decodeResponse() {
        int fileClientMessageIndex = 453;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        ReplicatedMapAddEntryListenerToKeyCodec.ResponseParameters parameters = ReplicatedMapAddEntryListenerToKeyCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aUUID, parameters.response));
    }

    private class ReplicatedMapAddEntryListenerToKeyCodecHandler extends ReplicatedMapAddEntryListenerToKeyCodec.AbstractEventHandler {
        @Override
        public void handleEntryEvent(com.hazelcast.nio.serialization.Data key, com.hazelcast.nio.serialization.Data value, com.hazelcast.nio.serialization.Data oldValue, com.hazelcast.nio.serialization.Data mergingValue, int eventType, java.util.UUID uuid, int numberOfAffectedEntries) {
            assertTrue(isEqual(aData, key));
            assertTrue(isEqual(aData, value));
            assertTrue(isEqual(aData, oldValue));
            assertTrue(isEqual(aData, mergingValue));
            assertTrue(isEqual(anInt, eventType));
            assertTrue(isEqual(aUUID, uuid));
            assertTrue(isEqual(anInt, numberOfAffectedEntries));
        }
    }

    @Test
    public void test_ReplicatedMapAddEntryListenerToKeyCodec_handleEntryEvent() {
        int fileClientMessageIndex = 454;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        ReplicatedMapAddEntryListenerToKeyCodecHandler handler = new ReplicatedMapAddEntryListenerToKeyCodecHandler();
        handler.handle(fromFile);
    }

    @Test
    public void test_ReplicatedMapAddEntryListenerCodec_encodeRequest() {
        int fileClientMessageIndex = 455;
        ClientMessage encoded = ReplicatedMapAddEntryListenerCodec.encodeRequest(aString, aBoolean);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_ReplicatedMapAddEntryListenerCodec_decodeResponse() {
        int fileClientMessageIndex = 456;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        ReplicatedMapAddEntryListenerCodec.ResponseParameters parameters = ReplicatedMapAddEntryListenerCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aUUID, parameters.response));
    }

    private class ReplicatedMapAddEntryListenerCodecHandler extends ReplicatedMapAddEntryListenerCodec.AbstractEventHandler {
        @Override
        public void handleEntryEvent(com.hazelcast.nio.serialization.Data key, com.hazelcast.nio.serialization.Data value, com.hazelcast.nio.serialization.Data oldValue, com.hazelcast.nio.serialization.Data mergingValue, int eventType, java.util.UUID uuid, int numberOfAffectedEntries) {
            assertTrue(isEqual(aData, key));
            assertTrue(isEqual(aData, value));
            assertTrue(isEqual(aData, oldValue));
            assertTrue(isEqual(aData, mergingValue));
            assertTrue(isEqual(anInt, eventType));
            assertTrue(isEqual(aUUID, uuid));
            assertTrue(isEqual(anInt, numberOfAffectedEntries));
        }
    }

    @Test
    public void test_ReplicatedMapAddEntryListenerCodec_handleEntryEvent() {
        int fileClientMessageIndex = 457;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        ReplicatedMapAddEntryListenerCodecHandler handler = new ReplicatedMapAddEntryListenerCodecHandler();
        handler.handle(fromFile);
    }

    @Test
    public void test_ReplicatedMapRemoveEntryListenerCodec_encodeRequest() {
        int fileClientMessageIndex = 458;
        ClientMessage encoded = ReplicatedMapRemoveEntryListenerCodec.encodeRequest(aString, aUUID);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_ReplicatedMapRemoveEntryListenerCodec_decodeResponse() {
        int fileClientMessageIndex = 459;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        ReplicatedMapRemoveEntryListenerCodec.ResponseParameters parameters = ReplicatedMapRemoveEntryListenerCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aBoolean, parameters.response));
    }

    @Test
    public void test_ReplicatedMapKeySetCodec_encodeRequest() {
        int fileClientMessageIndex = 460;
        ClientMessage encoded = ReplicatedMapKeySetCodec.encodeRequest(aString);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_ReplicatedMapKeySetCodec_decodeResponse() {
        int fileClientMessageIndex = 461;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        ReplicatedMapKeySetCodec.ResponseParameters parameters = ReplicatedMapKeySetCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aListOfData, parameters.response));
    }

    @Test
    public void test_ReplicatedMapValuesCodec_encodeRequest() {
        int fileClientMessageIndex = 462;
        ClientMessage encoded = ReplicatedMapValuesCodec.encodeRequest(aString);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_ReplicatedMapValuesCodec_decodeResponse() {
        int fileClientMessageIndex = 463;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        ReplicatedMapValuesCodec.ResponseParameters parameters = ReplicatedMapValuesCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aListOfData, parameters.response));
    }

    @Test
    public void test_ReplicatedMapEntrySetCodec_encodeRequest() {
        int fileClientMessageIndex = 464;
        ClientMessage encoded = ReplicatedMapEntrySetCodec.encodeRequest(aString);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_ReplicatedMapEntrySetCodec_decodeResponse() {
        int fileClientMessageIndex = 465;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        ReplicatedMapEntrySetCodec.ResponseParameters parameters = ReplicatedMapEntrySetCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aListOfDataToData, parameters.response));
    }

    @Test
    public void test_ReplicatedMapAddNearCacheEntryListenerCodec_encodeRequest() {
        int fileClientMessageIndex = 466;
        ClientMessage encoded = ReplicatedMapAddNearCacheEntryListenerCodec.encodeRequest(aString, aBoolean, aBoolean);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_ReplicatedMapAddNearCacheEntryListenerCodec_decodeResponse() {
        int fileClientMessageIndex = 467;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        ReplicatedMapAddNearCacheEntryListenerCodec.ResponseParameters parameters = ReplicatedMapAddNearCacheEntryListenerCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aUUID, parameters.response));
    }

    private class ReplicatedMapAddNearCacheEntryListenerCodecHandler extends ReplicatedMapAddNearCacheEntryListenerCodec.AbstractEventHandler {
        @Override
        public void handleEntryEvent(com.hazelcast.nio.serialization.Data key, com.hazelcast.nio.serialization.Data value, com.hazelcast.nio.serialization.Data oldValue, com.hazelcast.nio.serialization.Data mergingValue, int eventType, java.util.UUID uuid, int numberOfAffectedEntries) {
            assertTrue(isEqual(aData, key));
            assertTrue(isEqual(aData, value));
            assertTrue(isEqual(aData, oldValue));
            assertTrue(isEqual(aData, mergingValue));
            assertTrue(isEqual(anInt, eventType));
            assertTrue(isEqual(aUUID, uuid));
            assertTrue(isEqual(anInt, numberOfAffectedEntries));
        }
    }

    @Test
    public void test_ReplicatedMapAddNearCacheEntryListenerCodec_handleEntryEvent() {
        int fileClientMessageIndex = 468;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        ReplicatedMapAddNearCacheEntryListenerCodecHandler handler = new ReplicatedMapAddNearCacheEntryListenerCodecHandler();
        handler.handle(fromFile);
    }

    @Test
    public void test_TransactionalMapContainsKeyCodec_encodeRequest() {
        int fileClientMessageIndex = 469;
        ClientMessage encoded = TransactionalMapContainsKeyCodec.encodeRequest(aString, aUUID, aLong, aData);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_TransactionalMapContainsKeyCodec_decodeResponse() {
        int fileClientMessageIndex = 470;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        TransactionalMapContainsKeyCodec.ResponseParameters parameters = TransactionalMapContainsKeyCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aBoolean, parameters.response));
    }

    @Test
    public void test_TransactionalMapGetCodec_encodeRequest() {
        int fileClientMessageIndex = 471;
        ClientMessage encoded = TransactionalMapGetCodec.encodeRequest(aString, aUUID, aLong, aData);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_TransactionalMapGetCodec_decodeResponse() {
        int fileClientMessageIndex = 472;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        TransactionalMapGetCodec.ResponseParameters parameters = TransactionalMapGetCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aData, parameters.response));
    }

    @Test
    public void test_TransactionalMapGetForUpdateCodec_encodeRequest() {
        int fileClientMessageIndex = 473;
        ClientMessage encoded = TransactionalMapGetForUpdateCodec.encodeRequest(aString, aUUID, aLong, aData);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_TransactionalMapGetForUpdateCodec_decodeResponse() {
        int fileClientMessageIndex = 474;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        TransactionalMapGetForUpdateCodec.ResponseParameters parameters = TransactionalMapGetForUpdateCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aData, parameters.response));
    }

    @Test
    public void test_TransactionalMapSizeCodec_encodeRequest() {
        int fileClientMessageIndex = 475;
        ClientMessage encoded = TransactionalMapSizeCodec.encodeRequest(aString, aUUID, aLong);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_TransactionalMapSizeCodec_decodeResponse() {
        int fileClientMessageIndex = 476;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        TransactionalMapSizeCodec.ResponseParameters parameters = TransactionalMapSizeCodec.decodeResponse(fromFile);
        assertTrue(isEqual(anInt, parameters.response));
    }

    @Test
    public void test_TransactionalMapIsEmptyCodec_encodeRequest() {
        int fileClientMessageIndex = 477;
        ClientMessage encoded = TransactionalMapIsEmptyCodec.encodeRequest(aString, aUUID, aLong);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_TransactionalMapIsEmptyCodec_decodeResponse() {
        int fileClientMessageIndex = 478;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        TransactionalMapIsEmptyCodec.ResponseParameters parameters = TransactionalMapIsEmptyCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aBoolean, parameters.response));
    }

    @Test
    public void test_TransactionalMapPutCodec_encodeRequest() {
        int fileClientMessageIndex = 479;
        ClientMessage encoded = TransactionalMapPutCodec.encodeRequest(aString, aUUID, aLong, aData, aData, aLong);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_TransactionalMapPutCodec_decodeResponse() {
        int fileClientMessageIndex = 480;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        TransactionalMapPutCodec.ResponseParameters parameters = TransactionalMapPutCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aData, parameters.response));
    }

    @Test
    public void test_TransactionalMapSetCodec_encodeRequest() {
        int fileClientMessageIndex = 481;
        ClientMessage encoded = TransactionalMapSetCodec.encodeRequest(aString, aUUID, aLong, aData, aData);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_TransactionalMapSetCodec_decodeResponse() {
        int fileClientMessageIndex = 482;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        TransactionalMapSetCodec.ResponseParameters parameters = TransactionalMapSetCodec.decodeResponse(fromFile);
    }

    @Test
    public void test_TransactionalMapPutIfAbsentCodec_encodeRequest() {
        int fileClientMessageIndex = 483;
        ClientMessage encoded = TransactionalMapPutIfAbsentCodec.encodeRequest(aString, aUUID, aLong, aData, aData);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_TransactionalMapPutIfAbsentCodec_decodeResponse() {
        int fileClientMessageIndex = 484;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        TransactionalMapPutIfAbsentCodec.ResponseParameters parameters = TransactionalMapPutIfAbsentCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aData, parameters.response));
    }

    @Test
    public void test_TransactionalMapReplaceCodec_encodeRequest() {
        int fileClientMessageIndex = 485;
        ClientMessage encoded = TransactionalMapReplaceCodec.encodeRequest(aString, aUUID, aLong, aData, aData);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_TransactionalMapReplaceCodec_decodeResponse() {
        int fileClientMessageIndex = 486;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        TransactionalMapReplaceCodec.ResponseParameters parameters = TransactionalMapReplaceCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aData, parameters.response));
    }

    @Test
    public void test_TransactionalMapReplaceIfSameCodec_encodeRequest() {
        int fileClientMessageIndex = 487;
        ClientMessage encoded = TransactionalMapReplaceIfSameCodec.encodeRequest(aString, aUUID, aLong, aData, aData, aData);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_TransactionalMapReplaceIfSameCodec_decodeResponse() {
        int fileClientMessageIndex = 488;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        TransactionalMapReplaceIfSameCodec.ResponseParameters parameters = TransactionalMapReplaceIfSameCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aBoolean, parameters.response));
    }

    @Test
    public void test_TransactionalMapRemoveCodec_encodeRequest() {
        int fileClientMessageIndex = 489;
        ClientMessage encoded = TransactionalMapRemoveCodec.encodeRequest(aString, aUUID, aLong, aData);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_TransactionalMapRemoveCodec_decodeResponse() {
        int fileClientMessageIndex = 490;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        TransactionalMapRemoveCodec.ResponseParameters parameters = TransactionalMapRemoveCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aData, parameters.response));
    }

    @Test
    public void test_TransactionalMapDeleteCodec_encodeRequest() {
        int fileClientMessageIndex = 491;
        ClientMessage encoded = TransactionalMapDeleteCodec.encodeRequest(aString, aUUID, aLong, aData);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_TransactionalMapDeleteCodec_decodeResponse() {
        int fileClientMessageIndex = 492;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        TransactionalMapDeleteCodec.ResponseParameters parameters = TransactionalMapDeleteCodec.decodeResponse(fromFile);
    }

    @Test
    public void test_TransactionalMapRemoveIfSameCodec_encodeRequest() {
        int fileClientMessageIndex = 493;
        ClientMessage encoded = TransactionalMapRemoveIfSameCodec.encodeRequest(aString, aUUID, aLong, aData, aData);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_TransactionalMapRemoveIfSameCodec_decodeResponse() {
        int fileClientMessageIndex = 494;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        TransactionalMapRemoveIfSameCodec.ResponseParameters parameters = TransactionalMapRemoveIfSameCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aBoolean, parameters.response));
    }

    @Test
    public void test_TransactionalMapKeySetCodec_encodeRequest() {
        int fileClientMessageIndex = 495;
        ClientMessage encoded = TransactionalMapKeySetCodec.encodeRequest(aString, aUUID, aLong);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_TransactionalMapKeySetCodec_decodeResponse() {
        int fileClientMessageIndex = 496;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        TransactionalMapKeySetCodec.ResponseParameters parameters = TransactionalMapKeySetCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aListOfData, parameters.response));
    }

    @Test
    public void test_TransactionalMapKeySetWithPredicateCodec_encodeRequest() {
        int fileClientMessageIndex = 497;
        ClientMessage encoded = TransactionalMapKeySetWithPredicateCodec.encodeRequest(aString, aUUID, aLong, aData);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_TransactionalMapKeySetWithPredicateCodec_decodeResponse() {
        int fileClientMessageIndex = 498;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        TransactionalMapKeySetWithPredicateCodec.ResponseParameters parameters = TransactionalMapKeySetWithPredicateCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aListOfData, parameters.response));
    }

    @Test
    public void test_TransactionalMapValuesCodec_encodeRequest() {
        int fileClientMessageIndex = 499;
        ClientMessage encoded = TransactionalMapValuesCodec.encodeRequest(aString, aUUID, aLong);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_TransactionalMapValuesCodec_decodeResponse() {
        int fileClientMessageIndex = 500;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        TransactionalMapValuesCodec.ResponseParameters parameters = TransactionalMapValuesCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aListOfData, parameters.response));
    }

    @Test
    public void test_TransactionalMapValuesWithPredicateCodec_encodeRequest() {
        int fileClientMessageIndex = 501;
        ClientMessage encoded = TransactionalMapValuesWithPredicateCodec.encodeRequest(aString, aUUID, aLong, aData);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_TransactionalMapValuesWithPredicateCodec_decodeResponse() {
        int fileClientMessageIndex = 502;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        TransactionalMapValuesWithPredicateCodec.ResponseParameters parameters = TransactionalMapValuesWithPredicateCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aListOfData, parameters.response));
    }

    @Test
    public void test_TransactionalMapContainsValueCodec_encodeRequest() {
        int fileClientMessageIndex = 503;
        ClientMessage encoded = TransactionalMapContainsValueCodec.encodeRequest(aString, aUUID, aLong, aData);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_TransactionalMapContainsValueCodec_decodeResponse() {
        int fileClientMessageIndex = 504;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        TransactionalMapContainsValueCodec.ResponseParameters parameters = TransactionalMapContainsValueCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aBoolean, parameters.response));
    }

    @Test
    public void test_TransactionalMultiMapPutCodec_encodeRequest() {
        int fileClientMessageIndex = 505;
        ClientMessage encoded = TransactionalMultiMapPutCodec.encodeRequest(aString, aUUID, aLong, aData, aData);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_TransactionalMultiMapPutCodec_decodeResponse() {
        int fileClientMessageIndex = 506;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        TransactionalMultiMapPutCodec.ResponseParameters parameters = TransactionalMultiMapPutCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aBoolean, parameters.response));
    }

    @Test
    public void test_TransactionalMultiMapGetCodec_encodeRequest() {
        int fileClientMessageIndex = 507;
        ClientMessage encoded = TransactionalMultiMapGetCodec.encodeRequest(aString, aUUID, aLong, aData);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_TransactionalMultiMapGetCodec_decodeResponse() {
        int fileClientMessageIndex = 508;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        TransactionalMultiMapGetCodec.ResponseParameters parameters = TransactionalMultiMapGetCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aListOfData, parameters.response));
    }

    @Test
    public void test_TransactionalMultiMapRemoveCodec_encodeRequest() {
        int fileClientMessageIndex = 509;
        ClientMessage encoded = TransactionalMultiMapRemoveCodec.encodeRequest(aString, aUUID, aLong, aData);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_TransactionalMultiMapRemoveCodec_decodeResponse() {
        int fileClientMessageIndex = 510;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        TransactionalMultiMapRemoveCodec.ResponseParameters parameters = TransactionalMultiMapRemoveCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aListOfData, parameters.response));
    }

    @Test
    public void test_TransactionalMultiMapRemoveEntryCodec_encodeRequest() {
        int fileClientMessageIndex = 511;
        ClientMessage encoded = TransactionalMultiMapRemoveEntryCodec.encodeRequest(aString, aUUID, aLong, aData, aData);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_TransactionalMultiMapRemoveEntryCodec_decodeResponse() {
        int fileClientMessageIndex = 512;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        TransactionalMultiMapRemoveEntryCodec.ResponseParameters parameters = TransactionalMultiMapRemoveEntryCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aBoolean, parameters.response));
    }

    @Test
    public void test_TransactionalMultiMapValueCountCodec_encodeRequest() {
        int fileClientMessageIndex = 513;
        ClientMessage encoded = TransactionalMultiMapValueCountCodec.encodeRequest(aString, aUUID, aLong, aData);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_TransactionalMultiMapValueCountCodec_decodeResponse() {
        int fileClientMessageIndex = 514;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        TransactionalMultiMapValueCountCodec.ResponseParameters parameters = TransactionalMultiMapValueCountCodec.decodeResponse(fromFile);
        assertTrue(isEqual(anInt, parameters.response));
    }

    @Test
    public void test_TransactionalMultiMapSizeCodec_encodeRequest() {
        int fileClientMessageIndex = 515;
        ClientMessage encoded = TransactionalMultiMapSizeCodec.encodeRequest(aString, aUUID, aLong);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_TransactionalMultiMapSizeCodec_decodeResponse() {
        int fileClientMessageIndex = 516;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        TransactionalMultiMapSizeCodec.ResponseParameters parameters = TransactionalMultiMapSizeCodec.decodeResponse(fromFile);
        assertTrue(isEqual(anInt, parameters.response));
    }

    @Test
    public void test_TransactionalSetAddCodec_encodeRequest() {
        int fileClientMessageIndex = 517;
        ClientMessage encoded = TransactionalSetAddCodec.encodeRequest(aString, aUUID, aLong, aData);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_TransactionalSetAddCodec_decodeResponse() {
        int fileClientMessageIndex = 518;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        TransactionalSetAddCodec.ResponseParameters parameters = TransactionalSetAddCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aBoolean, parameters.response));
    }

    @Test
    public void test_TransactionalSetRemoveCodec_encodeRequest() {
        int fileClientMessageIndex = 519;
        ClientMessage encoded = TransactionalSetRemoveCodec.encodeRequest(aString, aUUID, aLong, aData);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_TransactionalSetRemoveCodec_decodeResponse() {
        int fileClientMessageIndex = 520;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        TransactionalSetRemoveCodec.ResponseParameters parameters = TransactionalSetRemoveCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aBoolean, parameters.response));
    }

    @Test
    public void test_TransactionalSetSizeCodec_encodeRequest() {
        int fileClientMessageIndex = 521;
        ClientMessage encoded = TransactionalSetSizeCodec.encodeRequest(aString, aUUID, aLong);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_TransactionalSetSizeCodec_decodeResponse() {
        int fileClientMessageIndex = 522;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        TransactionalSetSizeCodec.ResponseParameters parameters = TransactionalSetSizeCodec.decodeResponse(fromFile);
        assertTrue(isEqual(anInt, parameters.response));
    }

    @Test
    public void test_TransactionalListAddCodec_encodeRequest() {
        int fileClientMessageIndex = 523;
        ClientMessage encoded = TransactionalListAddCodec.encodeRequest(aString, aUUID, aLong, aData);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_TransactionalListAddCodec_decodeResponse() {
        int fileClientMessageIndex = 524;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        TransactionalListAddCodec.ResponseParameters parameters = TransactionalListAddCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aBoolean, parameters.response));
    }

    @Test
    public void test_TransactionalListRemoveCodec_encodeRequest() {
        int fileClientMessageIndex = 525;
        ClientMessage encoded = TransactionalListRemoveCodec.encodeRequest(aString, aUUID, aLong, aData);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_TransactionalListRemoveCodec_decodeResponse() {
        int fileClientMessageIndex = 526;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        TransactionalListRemoveCodec.ResponseParameters parameters = TransactionalListRemoveCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aBoolean, parameters.response));
    }

    @Test
    public void test_TransactionalListSizeCodec_encodeRequest() {
        int fileClientMessageIndex = 527;
        ClientMessage encoded = TransactionalListSizeCodec.encodeRequest(aString, aUUID, aLong);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_TransactionalListSizeCodec_decodeResponse() {
        int fileClientMessageIndex = 528;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        TransactionalListSizeCodec.ResponseParameters parameters = TransactionalListSizeCodec.decodeResponse(fromFile);
        assertTrue(isEqual(anInt, parameters.response));
    }

    @Test
    public void test_TransactionalQueueOfferCodec_encodeRequest() {
        int fileClientMessageIndex = 529;
        ClientMessage encoded = TransactionalQueueOfferCodec.encodeRequest(aString, aUUID, aLong, aData, aLong);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_TransactionalQueueOfferCodec_decodeResponse() {
        int fileClientMessageIndex = 530;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        TransactionalQueueOfferCodec.ResponseParameters parameters = TransactionalQueueOfferCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aBoolean, parameters.response));
    }

    @Test
    public void test_TransactionalQueueTakeCodec_encodeRequest() {
        int fileClientMessageIndex = 531;
        ClientMessage encoded = TransactionalQueueTakeCodec.encodeRequest(aString, aUUID, aLong);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_TransactionalQueueTakeCodec_decodeResponse() {
        int fileClientMessageIndex = 532;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        TransactionalQueueTakeCodec.ResponseParameters parameters = TransactionalQueueTakeCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aData, parameters.response));
    }

    @Test
    public void test_TransactionalQueuePollCodec_encodeRequest() {
        int fileClientMessageIndex = 533;
        ClientMessage encoded = TransactionalQueuePollCodec.encodeRequest(aString, aUUID, aLong, aLong);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_TransactionalQueuePollCodec_decodeResponse() {
        int fileClientMessageIndex = 534;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        TransactionalQueuePollCodec.ResponseParameters parameters = TransactionalQueuePollCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aData, parameters.response));
    }

    @Test
    public void test_TransactionalQueuePeekCodec_encodeRequest() {
        int fileClientMessageIndex = 535;
        ClientMessage encoded = TransactionalQueuePeekCodec.encodeRequest(aString, aUUID, aLong, aLong);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_TransactionalQueuePeekCodec_decodeResponse() {
        int fileClientMessageIndex = 536;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        TransactionalQueuePeekCodec.ResponseParameters parameters = TransactionalQueuePeekCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aData, parameters.response));
    }

    @Test
    public void test_TransactionalQueueSizeCodec_encodeRequest() {
        int fileClientMessageIndex = 537;
        ClientMessage encoded = TransactionalQueueSizeCodec.encodeRequest(aString, aUUID, aLong);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_TransactionalQueueSizeCodec_decodeResponse() {
        int fileClientMessageIndex = 538;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        TransactionalQueueSizeCodec.ResponseParameters parameters = TransactionalQueueSizeCodec.decodeResponse(fromFile);
        assertTrue(isEqual(anInt, parameters.response));
    }

    @Test
    public void test_CacheAddEntryListenerCodec_encodeRequest() {
        int fileClientMessageIndex = 539;
        ClientMessage encoded = CacheAddEntryListenerCodec.encodeRequest(aString, aBoolean);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_CacheAddEntryListenerCodec_decodeResponse() {
        int fileClientMessageIndex = 540;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        CacheAddEntryListenerCodec.ResponseParameters parameters = CacheAddEntryListenerCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aUUID, parameters.response));
    }

    private class CacheAddEntryListenerCodecHandler extends CacheAddEntryListenerCodec.AbstractEventHandler {
        @Override
        public void handleCacheEvent(int type, java.util.Collection<com.hazelcast.cache.impl.CacheEventData> keys, int completionId) {
            assertTrue(isEqual(anInt, type));
            assertTrue(isEqual(aListOfCacheEventData, keys));
            assertTrue(isEqual(anInt, completionId));
        }
    }

    @Test
    public void test_CacheAddEntryListenerCodec_handleCacheEvent() {
        int fileClientMessageIndex = 541;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        CacheAddEntryListenerCodecHandler handler = new CacheAddEntryListenerCodecHandler();
        handler.handle(fromFile);
    }

    @Test
    public void test_CacheClearCodec_encodeRequest() {
        int fileClientMessageIndex = 542;
        ClientMessage encoded = CacheClearCodec.encodeRequest(aString);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_CacheClearCodec_decodeResponse() {
        int fileClientMessageIndex = 543;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        CacheClearCodec.ResponseParameters parameters = CacheClearCodec.decodeResponse(fromFile);
    }

    @Test
    public void test_CacheRemoveAllKeysCodec_encodeRequest() {
        int fileClientMessageIndex = 544;
        ClientMessage encoded = CacheRemoveAllKeysCodec.encodeRequest(aString, aListOfData, anInt);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_CacheRemoveAllKeysCodec_decodeResponse() {
        int fileClientMessageIndex = 545;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        CacheRemoveAllKeysCodec.ResponseParameters parameters = CacheRemoveAllKeysCodec.decodeResponse(fromFile);
    }

    @Test
    public void test_CacheRemoveAllCodec_encodeRequest() {
        int fileClientMessageIndex = 546;
        ClientMessage encoded = CacheRemoveAllCodec.encodeRequest(aString, anInt);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_CacheRemoveAllCodec_decodeResponse() {
        int fileClientMessageIndex = 547;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        CacheRemoveAllCodec.ResponseParameters parameters = CacheRemoveAllCodec.decodeResponse(fromFile);
    }

    @Test
    public void test_CacheContainsKeyCodec_encodeRequest() {
        int fileClientMessageIndex = 548;
        ClientMessage encoded = CacheContainsKeyCodec.encodeRequest(aString, aData);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_CacheContainsKeyCodec_decodeResponse() {
        int fileClientMessageIndex = 549;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        CacheContainsKeyCodec.ResponseParameters parameters = CacheContainsKeyCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aBoolean, parameters.response));
    }

    @Test
    public void test_CacheCreateConfigCodec_encodeRequest() {
        int fileClientMessageIndex = 550;
        ClientMessage encoded = CacheCreateConfigCodec.encodeRequest(aCacheConfigHolder, aBoolean);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_CacheCreateConfigCodec_decodeResponse() {
        int fileClientMessageIndex = 551;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        CacheCreateConfigCodec.ResponseParameters parameters = CacheCreateConfigCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aCacheConfigHolder, parameters.response));
    }

    @Test
    public void test_CacheDestroyCodec_encodeRequest() {
        int fileClientMessageIndex = 552;
        ClientMessage encoded = CacheDestroyCodec.encodeRequest(aString);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_CacheDestroyCodec_decodeResponse() {
        int fileClientMessageIndex = 553;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        CacheDestroyCodec.ResponseParameters parameters = CacheDestroyCodec.decodeResponse(fromFile);
    }

    @Test
    public void test_CacheEntryProcessorCodec_encodeRequest() {
        int fileClientMessageIndex = 554;
        ClientMessage encoded = CacheEntryProcessorCodec.encodeRequest(aString, aData, aData, aListOfData, anInt);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_CacheEntryProcessorCodec_decodeResponse() {
        int fileClientMessageIndex = 555;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        CacheEntryProcessorCodec.ResponseParameters parameters = CacheEntryProcessorCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aData, parameters.response));
    }

    @Test
    public void test_CacheGetAllCodec_encodeRequest() {
        int fileClientMessageIndex = 556;
        ClientMessage encoded = CacheGetAllCodec.encodeRequest(aString, aListOfData, aData);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_CacheGetAllCodec_decodeResponse() {
        int fileClientMessageIndex = 557;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        CacheGetAllCodec.ResponseParameters parameters = CacheGetAllCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aListOfDataToData, parameters.response));
    }

    @Test
    public void test_CacheGetAndRemoveCodec_encodeRequest() {
        int fileClientMessageIndex = 558;
        ClientMessage encoded = CacheGetAndRemoveCodec.encodeRequest(aString, aData, anInt);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_CacheGetAndRemoveCodec_decodeResponse() {
        int fileClientMessageIndex = 559;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        CacheGetAndRemoveCodec.ResponseParameters parameters = CacheGetAndRemoveCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aData, parameters.response));
    }

    @Test
    public void test_CacheGetAndReplaceCodec_encodeRequest() {
        int fileClientMessageIndex = 560;
        ClientMessage encoded = CacheGetAndReplaceCodec.encodeRequest(aString, aData, aData, aData, anInt);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_CacheGetAndReplaceCodec_decodeResponse() {
        int fileClientMessageIndex = 561;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        CacheGetAndReplaceCodec.ResponseParameters parameters = CacheGetAndReplaceCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aData, parameters.response));
    }

    @Test
    public void test_CacheGetConfigCodec_encodeRequest() {
        int fileClientMessageIndex = 562;
        ClientMessage encoded = CacheGetConfigCodec.encodeRequest(aString, aString);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_CacheGetConfigCodec_decodeResponse() {
        int fileClientMessageIndex = 563;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        CacheGetConfigCodec.ResponseParameters parameters = CacheGetConfigCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aCacheConfigHolder, parameters.response));
    }

    @Test
    public void test_CacheGetCodec_encodeRequest() {
        int fileClientMessageIndex = 564;
        ClientMessage encoded = CacheGetCodec.encodeRequest(aString, aData, aData);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_CacheGetCodec_decodeResponse() {
        int fileClientMessageIndex = 565;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        CacheGetCodec.ResponseParameters parameters = CacheGetCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aData, parameters.response));
    }

    @Test
    public void test_CacheIterateCodec_encodeRequest() {
        int fileClientMessageIndex = 566;
        ClientMessage encoded = CacheIterateCodec.encodeRequest(aString, anInt, anInt);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_CacheIterateCodec_decodeResponse() {
        int fileClientMessageIndex = 567;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        CacheIterateCodec.ResponseParameters parameters = CacheIterateCodec.decodeResponse(fromFile);
        assertTrue(isEqual(anInt, parameters.tableIndex));
        assertTrue(isEqual(aListOfData, parameters.keys));
    }

    @Test
    public void test_CacheListenerRegistrationCodec_encodeRequest() {
        int fileClientMessageIndex = 568;
        ClientMessage encoded = CacheListenerRegistrationCodec.encodeRequest(aString, aData, aBoolean, anAddress);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_CacheListenerRegistrationCodec_decodeResponse() {
        int fileClientMessageIndex = 569;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        CacheListenerRegistrationCodec.ResponseParameters parameters = CacheListenerRegistrationCodec.decodeResponse(fromFile);
    }

    @Test
    public void test_CacheLoadAllCodec_encodeRequest() {
        int fileClientMessageIndex = 570;
        ClientMessage encoded = CacheLoadAllCodec.encodeRequest(aString, aListOfData, aBoolean);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_CacheLoadAllCodec_decodeResponse() {
        int fileClientMessageIndex = 571;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        CacheLoadAllCodec.ResponseParameters parameters = CacheLoadAllCodec.decodeResponse(fromFile);
    }

    @Test
    public void test_CacheManagementConfigCodec_encodeRequest() {
        int fileClientMessageIndex = 572;
        ClientMessage encoded = CacheManagementConfigCodec.encodeRequest(aString, aBoolean, aBoolean, anAddress);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_CacheManagementConfigCodec_decodeResponse() {
        int fileClientMessageIndex = 573;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        CacheManagementConfigCodec.ResponseParameters parameters = CacheManagementConfigCodec.decodeResponse(fromFile);
    }

    @Test
    public void test_CachePutIfAbsentCodec_encodeRequest() {
        int fileClientMessageIndex = 574;
        ClientMessage encoded = CachePutIfAbsentCodec.encodeRequest(aString, aData, aData, aData, anInt);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_CachePutIfAbsentCodec_decodeResponse() {
        int fileClientMessageIndex = 575;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        CachePutIfAbsentCodec.ResponseParameters parameters = CachePutIfAbsentCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aBoolean, parameters.response));
    }

    @Test
    public void test_CachePutCodec_encodeRequest() {
        int fileClientMessageIndex = 576;
        ClientMessage encoded = CachePutCodec.encodeRequest(aString, aData, aData, aData, aBoolean, anInt);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_CachePutCodec_decodeResponse() {
        int fileClientMessageIndex = 577;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        CachePutCodec.ResponseParameters parameters = CachePutCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aData, parameters.response));
    }

    @Test
    public void test_CacheRemoveEntryListenerCodec_encodeRequest() {
        int fileClientMessageIndex = 578;
        ClientMessage encoded = CacheRemoveEntryListenerCodec.encodeRequest(aString, aUUID);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_CacheRemoveEntryListenerCodec_decodeResponse() {
        int fileClientMessageIndex = 579;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        CacheRemoveEntryListenerCodec.ResponseParameters parameters = CacheRemoveEntryListenerCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aBoolean, parameters.response));
    }

    @Test
    public void test_CacheRemoveInvalidationListenerCodec_encodeRequest() {
        int fileClientMessageIndex = 580;
        ClientMessage encoded = CacheRemoveInvalidationListenerCodec.encodeRequest(aString, aUUID);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_CacheRemoveInvalidationListenerCodec_decodeResponse() {
        int fileClientMessageIndex = 581;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        CacheRemoveInvalidationListenerCodec.ResponseParameters parameters = CacheRemoveInvalidationListenerCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aBoolean, parameters.response));
    }

    @Test
    public void test_CacheRemoveCodec_encodeRequest() {
        int fileClientMessageIndex = 582;
        ClientMessage encoded = CacheRemoveCodec.encodeRequest(aString, aData, aData, anInt);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_CacheRemoveCodec_decodeResponse() {
        int fileClientMessageIndex = 583;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        CacheRemoveCodec.ResponseParameters parameters = CacheRemoveCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aBoolean, parameters.response));
    }

    @Test
    public void test_CacheReplaceCodec_encodeRequest() {
        int fileClientMessageIndex = 584;
        ClientMessage encoded = CacheReplaceCodec.encodeRequest(aString, aData, aData, aData, aData, anInt);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_CacheReplaceCodec_decodeResponse() {
        int fileClientMessageIndex = 585;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        CacheReplaceCodec.ResponseParameters parameters = CacheReplaceCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aData, parameters.response));
    }

    @Test
    public void test_CacheSizeCodec_encodeRequest() {
        int fileClientMessageIndex = 586;
        ClientMessage encoded = CacheSizeCodec.encodeRequest(aString);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_CacheSizeCodec_decodeResponse() {
        int fileClientMessageIndex = 587;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        CacheSizeCodec.ResponseParameters parameters = CacheSizeCodec.decodeResponse(fromFile);
        assertTrue(isEqual(anInt, parameters.response));
    }

    @Test
    public void test_CacheAddPartitionLostListenerCodec_encodeRequest() {
        int fileClientMessageIndex = 588;
        ClientMessage encoded = CacheAddPartitionLostListenerCodec.encodeRequest(aString, aBoolean);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_CacheAddPartitionLostListenerCodec_decodeResponse() {
        int fileClientMessageIndex = 589;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        CacheAddPartitionLostListenerCodec.ResponseParameters parameters = CacheAddPartitionLostListenerCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aUUID, parameters.response));
    }

    private class CacheAddPartitionLostListenerCodecHandler extends CacheAddPartitionLostListenerCodec.AbstractEventHandler {
        @Override
        public void handleCachePartitionLostEvent(int partitionId, java.util.UUID uuid) {
            assertTrue(isEqual(anInt, partitionId));
            assertTrue(isEqual(aUUID, uuid));
        }
    }

    @Test
    public void test_CacheAddPartitionLostListenerCodec_handleCachePartitionLostEvent() {
        int fileClientMessageIndex = 590;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        CacheAddPartitionLostListenerCodecHandler handler = new CacheAddPartitionLostListenerCodecHandler();
        handler.handle(fromFile);
    }

    @Test
    public void test_CacheRemovePartitionLostListenerCodec_encodeRequest() {
        int fileClientMessageIndex = 591;
        ClientMessage encoded = CacheRemovePartitionLostListenerCodec.encodeRequest(aString, aUUID);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_CacheRemovePartitionLostListenerCodec_decodeResponse() {
        int fileClientMessageIndex = 592;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        CacheRemovePartitionLostListenerCodec.ResponseParameters parameters = CacheRemovePartitionLostListenerCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aBoolean, parameters.response));
    }

    @Test
    public void test_CachePutAllCodec_encodeRequest() {
        int fileClientMessageIndex = 593;
        ClientMessage encoded = CachePutAllCodec.encodeRequest(aString, aListOfDataToData, aData, anInt);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_CachePutAllCodec_decodeResponse() {
        int fileClientMessageIndex = 594;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        CachePutAllCodec.ResponseParameters parameters = CachePutAllCodec.decodeResponse(fromFile);
    }

    @Test
    public void test_CacheIterateEntriesCodec_encodeRequest() {
        int fileClientMessageIndex = 595;
        ClientMessage encoded = CacheIterateEntriesCodec.encodeRequest(aString, anInt, anInt);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_CacheIterateEntriesCodec_decodeResponse() {
        int fileClientMessageIndex = 596;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        CacheIterateEntriesCodec.ResponseParameters parameters = CacheIterateEntriesCodec.decodeResponse(fromFile);
        assertTrue(isEqual(anInt, parameters.tableIndex));
        assertTrue(isEqual(aListOfDataToData, parameters.entries));
    }

    @Test
    public void test_CacheAddNearCacheInvalidationListenerCodec_encodeRequest() {
        int fileClientMessageIndex = 597;
        ClientMessage encoded = CacheAddNearCacheInvalidationListenerCodec.encodeRequest(aString, aBoolean);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_CacheAddNearCacheInvalidationListenerCodec_decodeResponse() {
        int fileClientMessageIndex = 598;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        CacheAddNearCacheInvalidationListenerCodec.ResponseParameters parameters = CacheAddNearCacheInvalidationListenerCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aUUID, parameters.response));
    }

    private class CacheAddNearCacheInvalidationListenerCodecHandler extends CacheAddNearCacheInvalidationListenerCodec.AbstractEventHandler {
        @Override
        public void handleCacheInvalidationEvent(java.lang.String name, com.hazelcast.nio.serialization.Data key, java.util.UUID sourceUuid, java.util.UUID partitionUuid, long sequence) {
            assertTrue(isEqual(aString, name));
            assertTrue(isEqual(aData, key));
            assertTrue(isEqual(aUUID, sourceUuid));
            assertTrue(isEqual(aUUID, partitionUuid));
            assertTrue(isEqual(aLong, sequence));
        }
        @Override
        public void handleCacheBatchInvalidationEvent(java.lang.String name, java.util.Collection<com.hazelcast.nio.serialization.Data> keys, java.util.Collection<java.util.UUID> sourceUuids, java.util.Collection<java.util.UUID> partitionUuids, java.util.Collection<java.lang.Long> sequences) {
            assertTrue(isEqual(aString, name));
            assertTrue(isEqual(aListOfData, keys));
            assertTrue(isEqual(aListOfUUIDs, sourceUuids));
            assertTrue(isEqual(aListOfUUIDs, partitionUuids));
            assertTrue(isEqual(aListOfLongs, sequences));
        }
    }

    @Test
    public void test_CacheAddNearCacheInvalidationListenerCodec_handleCacheInvalidationEvent() {
        int fileClientMessageIndex = 599;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        CacheAddNearCacheInvalidationListenerCodecHandler handler = new CacheAddNearCacheInvalidationListenerCodecHandler();
        handler.handle(fromFile);
    }

    @Test
    public void test_CacheAddNearCacheInvalidationListenerCodec_handleCacheBatchInvalidationEvent() {
        int fileClientMessageIndex = 600;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        CacheAddNearCacheInvalidationListenerCodecHandler handler = new CacheAddNearCacheInvalidationListenerCodecHandler();
        handler.handle(fromFile);
    }

    @Test
    public void test_CacheFetchNearCacheInvalidationMetadataCodec_encodeRequest() {
        int fileClientMessageIndex = 601;
        ClientMessage encoded = CacheFetchNearCacheInvalidationMetadataCodec.encodeRequest(aListOfStrings, anAddress);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_CacheFetchNearCacheInvalidationMetadataCodec_decodeResponse() {
        int fileClientMessageIndex = 602;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        CacheFetchNearCacheInvalidationMetadataCodec.ResponseParameters parameters = CacheFetchNearCacheInvalidationMetadataCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aListOfStringToListOfIntegerToLong, parameters.namePartitionSequenceList));
        assertTrue(isEqual(aListOfIntegerToUUID, parameters.partitionUuidList));
    }

    @Test
    public void test_CacheAssignAndGetUuidsCodec_encodeRequest() {
        int fileClientMessageIndex = 603;
        ClientMessage encoded = CacheAssignAndGetUuidsCodec.encodeRequest();
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_CacheAssignAndGetUuidsCodec_decodeResponse() {
        int fileClientMessageIndex = 604;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        CacheAssignAndGetUuidsCodec.ResponseParameters parameters = CacheAssignAndGetUuidsCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aListOfIntegerToUUID, parameters.partitionUuidList));
    }

    @Test
    public void test_CacheEventJournalSubscribeCodec_encodeRequest() {
        int fileClientMessageIndex = 605;
        ClientMessage encoded = CacheEventJournalSubscribeCodec.encodeRequest(aString);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_CacheEventJournalSubscribeCodec_decodeResponse() {
        int fileClientMessageIndex = 606;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        CacheEventJournalSubscribeCodec.ResponseParameters parameters = CacheEventJournalSubscribeCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aLong, parameters.oldestSequence));
        assertTrue(isEqual(aLong, parameters.newestSequence));
    }

    @Test
    public void test_CacheEventJournalReadCodec_encodeRequest() {
        int fileClientMessageIndex = 607;
        ClientMessage encoded = CacheEventJournalReadCodec.encodeRequest(aString, aLong, anInt, anInt, aData, aData);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_CacheEventJournalReadCodec_decodeResponse() {
        int fileClientMessageIndex = 608;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        CacheEventJournalReadCodec.ResponseParameters parameters = CacheEventJournalReadCodec.decodeResponse(fromFile);
        assertTrue(isEqual(anInt, parameters.readCount));
        assertTrue(isEqual(aListOfData, parameters.items));
        assertTrue(isEqual(aLongArray, parameters.itemSeqs));
        assertTrue(isEqual(aLong, parameters.nextSeq));
    }

    @Test
    public void test_CacheSetExpiryPolicyCodec_encodeRequest() {
        int fileClientMessageIndex = 609;
        ClientMessage encoded = CacheSetExpiryPolicyCodec.encodeRequest(aString, aListOfData, aData);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_CacheSetExpiryPolicyCodec_decodeResponse() {
        int fileClientMessageIndex = 610;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        CacheSetExpiryPolicyCodec.ResponseParameters parameters = CacheSetExpiryPolicyCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aBoolean, parameters.response));
    }

    @Test
    public void test_XATransactionClearRemoteCodec_encodeRequest() {
        int fileClientMessageIndex = 611;
        ClientMessage encoded = XATransactionClearRemoteCodec.encodeRequest(anXid);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_XATransactionClearRemoteCodec_decodeResponse() {
        int fileClientMessageIndex = 612;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        XATransactionClearRemoteCodec.ResponseParameters parameters = XATransactionClearRemoteCodec.decodeResponse(fromFile);
    }

    @Test
    public void test_XATransactionCollectTransactionsCodec_encodeRequest() {
        int fileClientMessageIndex = 613;
        ClientMessage encoded = XATransactionCollectTransactionsCodec.encodeRequest();
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_XATransactionCollectTransactionsCodec_decodeResponse() {
        int fileClientMessageIndex = 614;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        XATransactionCollectTransactionsCodec.ResponseParameters parameters = XATransactionCollectTransactionsCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aListOfXids, parameters.response));
    }

    @Test
    public void test_XATransactionFinalizeCodec_encodeRequest() {
        int fileClientMessageIndex = 615;
        ClientMessage encoded = XATransactionFinalizeCodec.encodeRequest(anXid, aBoolean);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_XATransactionFinalizeCodec_decodeResponse() {
        int fileClientMessageIndex = 616;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        XATransactionFinalizeCodec.ResponseParameters parameters = XATransactionFinalizeCodec.decodeResponse(fromFile);
    }

    @Test
    public void test_XATransactionCommitCodec_encodeRequest() {
        int fileClientMessageIndex = 617;
        ClientMessage encoded = XATransactionCommitCodec.encodeRequest(aUUID, aBoolean);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_XATransactionCommitCodec_decodeResponse() {
        int fileClientMessageIndex = 618;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        XATransactionCommitCodec.ResponseParameters parameters = XATransactionCommitCodec.decodeResponse(fromFile);
    }

    @Test
    public void test_XATransactionCreateCodec_encodeRequest() {
        int fileClientMessageIndex = 619;
        ClientMessage encoded = XATransactionCreateCodec.encodeRequest(anXid, aLong);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_XATransactionCreateCodec_decodeResponse() {
        int fileClientMessageIndex = 620;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        XATransactionCreateCodec.ResponseParameters parameters = XATransactionCreateCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aUUID, parameters.response));
    }

    @Test
    public void test_XATransactionPrepareCodec_encodeRequest() {
        int fileClientMessageIndex = 621;
        ClientMessage encoded = XATransactionPrepareCodec.encodeRequest(aUUID);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_XATransactionPrepareCodec_decodeResponse() {
        int fileClientMessageIndex = 622;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        XATransactionPrepareCodec.ResponseParameters parameters = XATransactionPrepareCodec.decodeResponse(fromFile);
    }

    @Test
    public void test_XATransactionRollbackCodec_encodeRequest() {
        int fileClientMessageIndex = 623;
        ClientMessage encoded = XATransactionRollbackCodec.encodeRequest(aUUID);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_XATransactionRollbackCodec_decodeResponse() {
        int fileClientMessageIndex = 624;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        XATransactionRollbackCodec.ResponseParameters parameters = XATransactionRollbackCodec.decodeResponse(fromFile);
    }

    @Test
    public void test_TransactionCommitCodec_encodeRequest() {
        int fileClientMessageIndex = 625;
        ClientMessage encoded = TransactionCommitCodec.encodeRequest(aUUID, aLong);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_TransactionCommitCodec_decodeResponse() {
        int fileClientMessageIndex = 626;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        TransactionCommitCodec.ResponseParameters parameters = TransactionCommitCodec.decodeResponse(fromFile);
    }

    @Test
    public void test_TransactionCreateCodec_encodeRequest() {
        int fileClientMessageIndex = 627;
        ClientMessage encoded = TransactionCreateCodec.encodeRequest(aLong, anInt, anInt, aLong);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_TransactionCreateCodec_decodeResponse() {
        int fileClientMessageIndex = 628;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        TransactionCreateCodec.ResponseParameters parameters = TransactionCreateCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aUUID, parameters.response));
    }

    @Test
    public void test_TransactionRollbackCodec_encodeRequest() {
        int fileClientMessageIndex = 629;
        ClientMessage encoded = TransactionRollbackCodec.encodeRequest(aUUID, aLong);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_TransactionRollbackCodec_decodeResponse() {
        int fileClientMessageIndex = 630;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        TransactionRollbackCodec.ResponseParameters parameters = TransactionRollbackCodec.decodeResponse(fromFile);
    }

    @Test
    public void test_ContinuousQueryPublisherCreateWithValueCodec_encodeRequest() {
        int fileClientMessageIndex = 631;
        ClientMessage encoded = ContinuousQueryPublisherCreateWithValueCodec.encodeRequest(aString, aString, aData, anInt, anInt, aLong, aBoolean, aBoolean);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_ContinuousQueryPublisherCreateWithValueCodec_decodeResponse() {
        int fileClientMessageIndex = 632;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        ContinuousQueryPublisherCreateWithValueCodec.ResponseParameters parameters = ContinuousQueryPublisherCreateWithValueCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aListOfDataToData, parameters.response));
    }

    @Test
    public void test_ContinuousQueryPublisherCreateCodec_encodeRequest() {
        int fileClientMessageIndex = 633;
        ClientMessage encoded = ContinuousQueryPublisherCreateCodec.encodeRequest(aString, aString, aData, anInt, anInt, aLong, aBoolean, aBoolean);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_ContinuousQueryPublisherCreateCodec_decodeResponse() {
        int fileClientMessageIndex = 634;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        ContinuousQueryPublisherCreateCodec.ResponseParameters parameters = ContinuousQueryPublisherCreateCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aListOfData, parameters.response));
    }

    @Test
    public void test_ContinuousQueryMadePublishableCodec_encodeRequest() {
        int fileClientMessageIndex = 635;
        ClientMessage encoded = ContinuousQueryMadePublishableCodec.encodeRequest(aString, aString);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_ContinuousQueryMadePublishableCodec_decodeResponse() {
        int fileClientMessageIndex = 636;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        ContinuousQueryMadePublishableCodec.ResponseParameters parameters = ContinuousQueryMadePublishableCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aBoolean, parameters.response));
    }

    @Test
    public void test_ContinuousQueryAddListenerCodec_encodeRequest() {
        int fileClientMessageIndex = 637;
        ClientMessage encoded = ContinuousQueryAddListenerCodec.encodeRequest(aString, aBoolean);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_ContinuousQueryAddListenerCodec_decodeResponse() {
        int fileClientMessageIndex = 638;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        ContinuousQueryAddListenerCodec.ResponseParameters parameters = ContinuousQueryAddListenerCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aUUID, parameters.response));
    }

    private class ContinuousQueryAddListenerCodecHandler extends ContinuousQueryAddListenerCodec.AbstractEventHandler {
        @Override
        public void handleQueryCacheSingleEvent(com.hazelcast.map.impl.querycache.event.QueryCacheEventData data) {
            assertTrue(isEqual(aQueryCacheEventData, data));
        }
        @Override
        public void handleQueryCacheBatchEvent(java.util.Collection<com.hazelcast.map.impl.querycache.event.QueryCacheEventData> events, java.lang.String source, int partitionId) {
            assertTrue(isEqual(aListOfQueryCacheEventData, events));
            assertTrue(isEqual(aString, source));
            assertTrue(isEqual(anInt, partitionId));
        }
    }

    @Test
    public void test_ContinuousQueryAddListenerCodec_handleQueryCacheSingleEvent() {
        int fileClientMessageIndex = 639;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        ContinuousQueryAddListenerCodecHandler handler = new ContinuousQueryAddListenerCodecHandler();
        handler.handle(fromFile);
    }

    @Test
    public void test_ContinuousQueryAddListenerCodec_handleQueryCacheBatchEvent() {
        int fileClientMessageIndex = 640;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        ContinuousQueryAddListenerCodecHandler handler = new ContinuousQueryAddListenerCodecHandler();
        handler.handle(fromFile);
    }

    @Test
    public void test_ContinuousQuerySetReadCursorCodec_encodeRequest() {
        int fileClientMessageIndex = 641;
        ClientMessage encoded = ContinuousQuerySetReadCursorCodec.encodeRequest(aString, aString, aLong);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_ContinuousQuerySetReadCursorCodec_decodeResponse() {
        int fileClientMessageIndex = 642;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        ContinuousQuerySetReadCursorCodec.ResponseParameters parameters = ContinuousQuerySetReadCursorCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aBoolean, parameters.response));
    }

    @Test
    public void test_ContinuousQueryDestroyCacheCodec_encodeRequest() {
        int fileClientMessageIndex = 643;
        ClientMessage encoded = ContinuousQueryDestroyCacheCodec.encodeRequest(aString, aString);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_ContinuousQueryDestroyCacheCodec_decodeResponse() {
        int fileClientMessageIndex = 644;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        ContinuousQueryDestroyCacheCodec.ResponseParameters parameters = ContinuousQueryDestroyCacheCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aBoolean, parameters.response));
    }

    @Test
    public void test_RingbufferSizeCodec_encodeRequest() {
        int fileClientMessageIndex = 645;
        ClientMessage encoded = RingbufferSizeCodec.encodeRequest(aString);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_RingbufferSizeCodec_decodeResponse() {
        int fileClientMessageIndex = 646;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        RingbufferSizeCodec.ResponseParameters parameters = RingbufferSizeCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aLong, parameters.response));
    }

    @Test
    public void test_RingbufferTailSequenceCodec_encodeRequest() {
        int fileClientMessageIndex = 647;
        ClientMessage encoded = RingbufferTailSequenceCodec.encodeRequest(aString);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_RingbufferTailSequenceCodec_decodeResponse() {
        int fileClientMessageIndex = 648;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        RingbufferTailSequenceCodec.ResponseParameters parameters = RingbufferTailSequenceCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aLong, parameters.response));
    }

    @Test
    public void test_RingbufferHeadSequenceCodec_encodeRequest() {
        int fileClientMessageIndex = 649;
        ClientMessage encoded = RingbufferHeadSequenceCodec.encodeRequest(aString);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_RingbufferHeadSequenceCodec_decodeResponse() {
        int fileClientMessageIndex = 650;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        RingbufferHeadSequenceCodec.ResponseParameters parameters = RingbufferHeadSequenceCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aLong, parameters.response));
    }

    @Test
    public void test_RingbufferCapacityCodec_encodeRequest() {
        int fileClientMessageIndex = 651;
        ClientMessage encoded = RingbufferCapacityCodec.encodeRequest(aString);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_RingbufferCapacityCodec_decodeResponse() {
        int fileClientMessageIndex = 652;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        RingbufferCapacityCodec.ResponseParameters parameters = RingbufferCapacityCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aLong, parameters.response));
    }

    @Test
    public void test_RingbufferRemainingCapacityCodec_encodeRequest() {
        int fileClientMessageIndex = 653;
        ClientMessage encoded = RingbufferRemainingCapacityCodec.encodeRequest(aString);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_RingbufferRemainingCapacityCodec_decodeResponse() {
        int fileClientMessageIndex = 654;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        RingbufferRemainingCapacityCodec.ResponseParameters parameters = RingbufferRemainingCapacityCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aLong, parameters.response));
    }

    @Test
    public void test_RingbufferAddCodec_encodeRequest() {
        int fileClientMessageIndex = 655;
        ClientMessage encoded = RingbufferAddCodec.encodeRequest(aString, anInt, aData);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_RingbufferAddCodec_decodeResponse() {
        int fileClientMessageIndex = 656;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        RingbufferAddCodec.ResponseParameters parameters = RingbufferAddCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aLong, parameters.response));
    }

    @Test
    public void test_RingbufferReadOneCodec_encodeRequest() {
        int fileClientMessageIndex = 657;
        ClientMessage encoded = RingbufferReadOneCodec.encodeRequest(aString, aLong);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_RingbufferReadOneCodec_decodeResponse() {
        int fileClientMessageIndex = 658;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        RingbufferReadOneCodec.ResponseParameters parameters = RingbufferReadOneCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aData, parameters.response));
    }

    @Test
    public void test_RingbufferAddAllCodec_encodeRequest() {
        int fileClientMessageIndex = 659;
        ClientMessage encoded = RingbufferAddAllCodec.encodeRequest(aString, aListOfData, anInt);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_RingbufferAddAllCodec_decodeResponse() {
        int fileClientMessageIndex = 660;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        RingbufferAddAllCodec.ResponseParameters parameters = RingbufferAddAllCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aLong, parameters.response));
    }

    @Test
    public void test_RingbufferReadManyCodec_encodeRequest() {
        int fileClientMessageIndex = 661;
        ClientMessage encoded = RingbufferReadManyCodec.encodeRequest(aString, aLong, anInt, anInt, aData);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_RingbufferReadManyCodec_decodeResponse() {
        int fileClientMessageIndex = 662;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        RingbufferReadManyCodec.ResponseParameters parameters = RingbufferReadManyCodec.decodeResponse(fromFile);
        assertTrue(isEqual(anInt, parameters.readCount));
        assertTrue(isEqual(aListOfData, parameters.items));
        assertTrue(isEqual(aLongArray, parameters.itemSeqs));
        assertTrue(isEqual(aLong, parameters.nextSeq));
    }

    @Test
    public void test_DurableExecutorShutdownCodec_encodeRequest() {
        int fileClientMessageIndex = 663;
        ClientMessage encoded = DurableExecutorShutdownCodec.encodeRequest(aString);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_DurableExecutorShutdownCodec_decodeResponse() {
        int fileClientMessageIndex = 664;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        DurableExecutorShutdownCodec.ResponseParameters parameters = DurableExecutorShutdownCodec.decodeResponse(fromFile);
    }

    @Test
    public void test_DurableExecutorIsShutdownCodec_encodeRequest() {
        int fileClientMessageIndex = 665;
        ClientMessage encoded = DurableExecutorIsShutdownCodec.encodeRequest(aString);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_DurableExecutorIsShutdownCodec_decodeResponse() {
        int fileClientMessageIndex = 666;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        DurableExecutorIsShutdownCodec.ResponseParameters parameters = DurableExecutorIsShutdownCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aBoolean, parameters.response));
    }

    @Test
    public void test_DurableExecutorSubmitToPartitionCodec_encodeRequest() {
        int fileClientMessageIndex = 667;
        ClientMessage encoded = DurableExecutorSubmitToPartitionCodec.encodeRequest(aString, aData);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_DurableExecutorSubmitToPartitionCodec_decodeResponse() {
        int fileClientMessageIndex = 668;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        DurableExecutorSubmitToPartitionCodec.ResponseParameters parameters = DurableExecutorSubmitToPartitionCodec.decodeResponse(fromFile);
        assertTrue(isEqual(anInt, parameters.response));
    }

    @Test
    public void test_DurableExecutorRetrieveResultCodec_encodeRequest() {
        int fileClientMessageIndex = 669;
        ClientMessage encoded = DurableExecutorRetrieveResultCodec.encodeRequest(aString, anInt);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_DurableExecutorRetrieveResultCodec_decodeResponse() {
        int fileClientMessageIndex = 670;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        DurableExecutorRetrieveResultCodec.ResponseParameters parameters = DurableExecutorRetrieveResultCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aData, parameters.response));
    }

    @Test
    public void test_DurableExecutorDisposeResultCodec_encodeRequest() {
        int fileClientMessageIndex = 671;
        ClientMessage encoded = DurableExecutorDisposeResultCodec.encodeRequest(aString, anInt);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_DurableExecutorDisposeResultCodec_decodeResponse() {
        int fileClientMessageIndex = 672;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        DurableExecutorDisposeResultCodec.ResponseParameters parameters = DurableExecutorDisposeResultCodec.decodeResponse(fromFile);
    }

    @Test
    public void test_DurableExecutorRetrieveAndDisposeResultCodec_encodeRequest() {
        int fileClientMessageIndex = 673;
        ClientMessage encoded = DurableExecutorRetrieveAndDisposeResultCodec.encodeRequest(aString, anInt);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_DurableExecutorRetrieveAndDisposeResultCodec_decodeResponse() {
        int fileClientMessageIndex = 674;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        DurableExecutorRetrieveAndDisposeResultCodec.ResponseParameters parameters = DurableExecutorRetrieveAndDisposeResultCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aData, parameters.response));
    }

    @Test
    public void test_CardinalityEstimatorAddCodec_encodeRequest() {
        int fileClientMessageIndex = 675;
        ClientMessage encoded = CardinalityEstimatorAddCodec.encodeRequest(aString, aLong);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_CardinalityEstimatorAddCodec_decodeResponse() {
        int fileClientMessageIndex = 676;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        CardinalityEstimatorAddCodec.ResponseParameters parameters = CardinalityEstimatorAddCodec.decodeResponse(fromFile);
    }

    @Test
    public void test_CardinalityEstimatorEstimateCodec_encodeRequest() {
        int fileClientMessageIndex = 677;
        ClientMessage encoded = CardinalityEstimatorEstimateCodec.encodeRequest(aString);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_CardinalityEstimatorEstimateCodec_decodeResponse() {
        int fileClientMessageIndex = 678;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        CardinalityEstimatorEstimateCodec.ResponseParameters parameters = CardinalityEstimatorEstimateCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aLong, parameters.response));
    }

    @Test
    public void test_ScheduledExecutorShutdownCodec_encodeRequest() {
        int fileClientMessageIndex = 679;
        ClientMessage encoded = ScheduledExecutorShutdownCodec.encodeRequest(aString, anAddress);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_ScheduledExecutorShutdownCodec_decodeResponse() {
        int fileClientMessageIndex = 680;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        ScheduledExecutorShutdownCodec.ResponseParameters parameters = ScheduledExecutorShutdownCodec.decodeResponse(fromFile);
    }

    @Test
    public void test_ScheduledExecutorSubmitToPartitionCodec_encodeRequest() {
        int fileClientMessageIndex = 681;
        ClientMessage encoded = ScheduledExecutorSubmitToPartitionCodec.encodeRequest(aString, aByte, aString, aData, aLong, aLong);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_ScheduledExecutorSubmitToPartitionCodec_decodeResponse() {
        int fileClientMessageIndex = 682;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        ScheduledExecutorSubmitToPartitionCodec.ResponseParameters parameters = ScheduledExecutorSubmitToPartitionCodec.decodeResponse(fromFile);
    }

    @Test
    public void test_ScheduledExecutorSubmitToAddressCodec_encodeRequest() {
        int fileClientMessageIndex = 683;
        ClientMessage encoded = ScheduledExecutorSubmitToAddressCodec.encodeRequest(aString, anAddress, aByte, aString, aData, aLong, aLong);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_ScheduledExecutorSubmitToAddressCodec_decodeResponse() {
        int fileClientMessageIndex = 684;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        ScheduledExecutorSubmitToAddressCodec.ResponseParameters parameters = ScheduledExecutorSubmitToAddressCodec.decodeResponse(fromFile);
    }

    @Test
    public void test_ScheduledExecutorGetAllScheduledFuturesCodec_encodeRequest() {
        int fileClientMessageIndex = 685;
        ClientMessage encoded = ScheduledExecutorGetAllScheduledFuturesCodec.encodeRequest(aString);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_ScheduledExecutorGetAllScheduledFuturesCodec_decodeResponse() {
        int fileClientMessageIndex = 686;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        ScheduledExecutorGetAllScheduledFuturesCodec.ResponseParameters parameters = ScheduledExecutorGetAllScheduledFuturesCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aListOfMemberToListOfScheduledTaskHandlers, parameters.handlers));
    }

    @Test
    public void test_ScheduledExecutorGetStatsFromPartitionCodec_encodeRequest() {
        int fileClientMessageIndex = 687;
        ClientMessage encoded = ScheduledExecutorGetStatsFromPartitionCodec.encodeRequest(aString, aString);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_ScheduledExecutorGetStatsFromPartitionCodec_decodeResponse() {
        int fileClientMessageIndex = 688;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        ScheduledExecutorGetStatsFromPartitionCodec.ResponseParameters parameters = ScheduledExecutorGetStatsFromPartitionCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aLong, parameters.lastIdleTimeNanos));
        assertTrue(isEqual(aLong, parameters.totalIdleTimeNanos));
        assertTrue(isEqual(aLong, parameters.totalRuns));
        assertTrue(isEqual(aLong, parameters.totalRunTimeNanos));
        assertTrue(isEqual(aLong, parameters.lastRunDurationNanos));
    }

    @Test
    public void test_ScheduledExecutorGetStatsFromAddressCodec_encodeRequest() {
        int fileClientMessageIndex = 689;
        ClientMessage encoded = ScheduledExecutorGetStatsFromAddressCodec.encodeRequest(aString, aString, anAddress);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_ScheduledExecutorGetStatsFromAddressCodec_decodeResponse() {
        int fileClientMessageIndex = 690;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        ScheduledExecutorGetStatsFromAddressCodec.ResponseParameters parameters = ScheduledExecutorGetStatsFromAddressCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aLong, parameters.lastIdleTimeNanos));
        assertTrue(isEqual(aLong, parameters.totalIdleTimeNanos));
        assertTrue(isEqual(aLong, parameters.totalRuns));
        assertTrue(isEqual(aLong, parameters.totalRunTimeNanos));
        assertTrue(isEqual(aLong, parameters.lastRunDurationNanos));
    }

    @Test
    public void test_ScheduledExecutorGetDelayFromPartitionCodec_encodeRequest() {
        int fileClientMessageIndex = 691;
        ClientMessage encoded = ScheduledExecutorGetDelayFromPartitionCodec.encodeRequest(aString, aString);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_ScheduledExecutorGetDelayFromPartitionCodec_decodeResponse() {
        int fileClientMessageIndex = 692;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        ScheduledExecutorGetDelayFromPartitionCodec.ResponseParameters parameters = ScheduledExecutorGetDelayFromPartitionCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aLong, parameters.response));
    }

    @Test
    public void test_ScheduledExecutorGetDelayFromAddressCodec_encodeRequest() {
        int fileClientMessageIndex = 693;
        ClientMessage encoded = ScheduledExecutorGetDelayFromAddressCodec.encodeRequest(aString, aString, anAddress);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_ScheduledExecutorGetDelayFromAddressCodec_decodeResponse() {
        int fileClientMessageIndex = 694;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        ScheduledExecutorGetDelayFromAddressCodec.ResponseParameters parameters = ScheduledExecutorGetDelayFromAddressCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aLong, parameters.response));
    }

    @Test
    public void test_ScheduledExecutorCancelFromPartitionCodec_encodeRequest() {
        int fileClientMessageIndex = 695;
        ClientMessage encoded = ScheduledExecutorCancelFromPartitionCodec.encodeRequest(aString, aString, aBoolean);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_ScheduledExecutorCancelFromPartitionCodec_decodeResponse() {
        int fileClientMessageIndex = 696;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        ScheduledExecutorCancelFromPartitionCodec.ResponseParameters parameters = ScheduledExecutorCancelFromPartitionCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aBoolean, parameters.response));
    }

    @Test
    public void test_ScheduledExecutorCancelFromAddressCodec_encodeRequest() {
        int fileClientMessageIndex = 697;
        ClientMessage encoded = ScheduledExecutorCancelFromAddressCodec.encodeRequest(aString, aString, anAddress, aBoolean);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_ScheduledExecutorCancelFromAddressCodec_decodeResponse() {
        int fileClientMessageIndex = 698;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        ScheduledExecutorCancelFromAddressCodec.ResponseParameters parameters = ScheduledExecutorCancelFromAddressCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aBoolean, parameters.response));
    }

    @Test
    public void test_ScheduledExecutorIsCancelledFromPartitionCodec_encodeRequest() {
        int fileClientMessageIndex = 699;
        ClientMessage encoded = ScheduledExecutorIsCancelledFromPartitionCodec.encodeRequest(aString, aString);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_ScheduledExecutorIsCancelledFromPartitionCodec_decodeResponse() {
        int fileClientMessageIndex = 700;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        ScheduledExecutorIsCancelledFromPartitionCodec.ResponseParameters parameters = ScheduledExecutorIsCancelledFromPartitionCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aBoolean, parameters.response));
    }

    @Test
    public void test_ScheduledExecutorIsCancelledFromAddressCodec_encodeRequest() {
        int fileClientMessageIndex = 701;
        ClientMessage encoded = ScheduledExecutorIsCancelledFromAddressCodec.encodeRequest(aString, aString, anAddress);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_ScheduledExecutorIsCancelledFromAddressCodec_decodeResponse() {
        int fileClientMessageIndex = 702;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        ScheduledExecutorIsCancelledFromAddressCodec.ResponseParameters parameters = ScheduledExecutorIsCancelledFromAddressCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aBoolean, parameters.response));
    }

    @Test
    public void test_ScheduledExecutorIsDoneFromPartitionCodec_encodeRequest() {
        int fileClientMessageIndex = 703;
        ClientMessage encoded = ScheduledExecutorIsDoneFromPartitionCodec.encodeRequest(aString, aString);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_ScheduledExecutorIsDoneFromPartitionCodec_decodeResponse() {
        int fileClientMessageIndex = 704;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        ScheduledExecutorIsDoneFromPartitionCodec.ResponseParameters parameters = ScheduledExecutorIsDoneFromPartitionCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aBoolean, parameters.response));
    }

    @Test
    public void test_ScheduledExecutorIsDoneFromAddressCodec_encodeRequest() {
        int fileClientMessageIndex = 705;
        ClientMessage encoded = ScheduledExecutorIsDoneFromAddressCodec.encodeRequest(aString, aString, anAddress);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_ScheduledExecutorIsDoneFromAddressCodec_decodeResponse() {
        int fileClientMessageIndex = 706;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        ScheduledExecutorIsDoneFromAddressCodec.ResponseParameters parameters = ScheduledExecutorIsDoneFromAddressCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aBoolean, parameters.response));
    }

    @Test
    public void test_ScheduledExecutorGetResultFromPartitionCodec_encodeRequest() {
        int fileClientMessageIndex = 707;
        ClientMessage encoded = ScheduledExecutorGetResultFromPartitionCodec.encodeRequest(aString, aString);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_ScheduledExecutorGetResultFromPartitionCodec_decodeResponse() {
        int fileClientMessageIndex = 708;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        ScheduledExecutorGetResultFromPartitionCodec.ResponseParameters parameters = ScheduledExecutorGetResultFromPartitionCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aData, parameters.response));
    }

    @Test
    public void test_ScheduledExecutorGetResultFromAddressCodec_encodeRequest() {
        int fileClientMessageIndex = 709;
        ClientMessage encoded = ScheduledExecutorGetResultFromAddressCodec.encodeRequest(aString, aString, anAddress);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_ScheduledExecutorGetResultFromAddressCodec_decodeResponse() {
        int fileClientMessageIndex = 710;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        ScheduledExecutorGetResultFromAddressCodec.ResponseParameters parameters = ScheduledExecutorGetResultFromAddressCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aData, parameters.response));
    }

    @Test
    public void test_ScheduledExecutorDisposeFromPartitionCodec_encodeRequest() {
        int fileClientMessageIndex = 711;
        ClientMessage encoded = ScheduledExecutorDisposeFromPartitionCodec.encodeRequest(aString, aString);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_ScheduledExecutorDisposeFromPartitionCodec_decodeResponse() {
        int fileClientMessageIndex = 712;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        ScheduledExecutorDisposeFromPartitionCodec.ResponseParameters parameters = ScheduledExecutorDisposeFromPartitionCodec.decodeResponse(fromFile);
    }

    @Test
    public void test_ScheduledExecutorDisposeFromAddressCodec_encodeRequest() {
        int fileClientMessageIndex = 713;
        ClientMessage encoded = ScheduledExecutorDisposeFromAddressCodec.encodeRequest(aString, aString, anAddress);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_ScheduledExecutorDisposeFromAddressCodec_decodeResponse() {
        int fileClientMessageIndex = 714;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        ScheduledExecutorDisposeFromAddressCodec.ResponseParameters parameters = ScheduledExecutorDisposeFromAddressCodec.decodeResponse(fromFile);
    }

    @Test
    public void test_DynamicConfigAddMultiMapConfigCodec_encodeRequest() {
        int fileClientMessageIndex = 715;
        ClientMessage encoded = DynamicConfigAddMultiMapConfigCodec.encodeRequest(aString, aString, aListOfListenerConfigHolders, aBoolean, anInt, anInt, aBoolean, aString, aString, anInt);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_DynamicConfigAddMultiMapConfigCodec_decodeResponse() {
        int fileClientMessageIndex = 716;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        DynamicConfigAddMultiMapConfigCodec.ResponseParameters parameters = DynamicConfigAddMultiMapConfigCodec.decodeResponse(fromFile);
    }

    @Test
    public void test_DynamicConfigAddRingbufferConfigCodec_encodeRequest() {
        int fileClientMessageIndex = 717;
        ClientMessage encoded = DynamicConfigAddRingbufferConfigCodec.encodeRequest(aString, anInt, anInt, anInt, anInt, aString, aRingbufferStoreConfigHolder, aString, aString, anInt);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_DynamicConfigAddRingbufferConfigCodec_decodeResponse() {
        int fileClientMessageIndex = 718;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        DynamicConfigAddRingbufferConfigCodec.ResponseParameters parameters = DynamicConfigAddRingbufferConfigCodec.decodeResponse(fromFile);
    }

    @Test
    public void test_DynamicConfigAddCardinalityEstimatorConfigCodec_encodeRequest() {
        int fileClientMessageIndex = 719;
        ClientMessage encoded = DynamicConfigAddCardinalityEstimatorConfigCodec.encodeRequest(aString, anInt, anInt, aString, aString, anInt);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_DynamicConfigAddCardinalityEstimatorConfigCodec_decodeResponse() {
        int fileClientMessageIndex = 720;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        DynamicConfigAddCardinalityEstimatorConfigCodec.ResponseParameters parameters = DynamicConfigAddCardinalityEstimatorConfigCodec.decodeResponse(fromFile);
    }

    @Test
    public void test_DynamicConfigAddListConfigCodec_encodeRequest() {
        int fileClientMessageIndex = 721;
        ClientMessage encoded = DynamicConfigAddListConfigCodec.encodeRequest(aString, aListOfListenerConfigHolders, anInt, anInt, anInt, aBoolean, aString, aString, anInt);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_DynamicConfigAddListConfigCodec_decodeResponse() {
        int fileClientMessageIndex = 722;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        DynamicConfigAddListConfigCodec.ResponseParameters parameters = DynamicConfigAddListConfigCodec.decodeResponse(fromFile);
    }

    @Test
    public void test_DynamicConfigAddSetConfigCodec_encodeRequest() {
        int fileClientMessageIndex = 723;
        ClientMessage encoded = DynamicConfigAddSetConfigCodec.encodeRequest(aString, aListOfListenerConfigHolders, anInt, anInt, anInt, aBoolean, aString, aString, anInt);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_DynamicConfigAddSetConfigCodec_decodeResponse() {
        int fileClientMessageIndex = 724;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        DynamicConfigAddSetConfigCodec.ResponseParameters parameters = DynamicConfigAddSetConfigCodec.decodeResponse(fromFile);
    }

    @Test
    public void test_DynamicConfigAddReplicatedMapConfigCodec_encodeRequest() {
        int fileClientMessageIndex = 725;
        ClientMessage encoded = DynamicConfigAddReplicatedMapConfigCodec.encodeRequest(aString, aString, aBoolean, aBoolean, aString, aListOfListenerConfigHolders, aString, anInt);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_DynamicConfigAddReplicatedMapConfigCodec_decodeResponse() {
        int fileClientMessageIndex = 726;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        DynamicConfigAddReplicatedMapConfigCodec.ResponseParameters parameters = DynamicConfigAddReplicatedMapConfigCodec.decodeResponse(fromFile);
    }

    @Test
    public void test_DynamicConfigAddTopicConfigCodec_encodeRequest() {
        int fileClientMessageIndex = 727;
        ClientMessage encoded = DynamicConfigAddTopicConfigCodec.encodeRequest(aString, aBoolean, aBoolean, aBoolean, aListOfListenerConfigHolders);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_DynamicConfigAddTopicConfigCodec_decodeResponse() {
        int fileClientMessageIndex = 728;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        DynamicConfigAddTopicConfigCodec.ResponseParameters parameters = DynamicConfigAddTopicConfigCodec.decodeResponse(fromFile);
    }

    @Test
    public void test_DynamicConfigAddExecutorConfigCodec_encodeRequest() {
        int fileClientMessageIndex = 729;
        ClientMessage encoded = DynamicConfigAddExecutorConfigCodec.encodeRequest(aString, anInt, anInt, aBoolean, aString);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_DynamicConfigAddExecutorConfigCodec_decodeResponse() {
        int fileClientMessageIndex = 730;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        DynamicConfigAddExecutorConfigCodec.ResponseParameters parameters = DynamicConfigAddExecutorConfigCodec.decodeResponse(fromFile);
    }

    @Test
    public void test_DynamicConfigAddDurableExecutorConfigCodec_encodeRequest() {
        int fileClientMessageIndex = 731;
        ClientMessage encoded = DynamicConfigAddDurableExecutorConfigCodec.encodeRequest(aString, anInt, anInt, anInt, aString);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_DynamicConfigAddDurableExecutorConfigCodec_decodeResponse() {
        int fileClientMessageIndex = 732;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        DynamicConfigAddDurableExecutorConfigCodec.ResponseParameters parameters = DynamicConfigAddDurableExecutorConfigCodec.decodeResponse(fromFile);
    }

    @Test
    public void test_DynamicConfigAddScheduledExecutorConfigCodec_encodeRequest() {
        int fileClientMessageIndex = 733;
        ClientMessage encoded = DynamicConfigAddScheduledExecutorConfigCodec.encodeRequest(aString, anInt, anInt, anInt, aString, aString, anInt);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_DynamicConfigAddScheduledExecutorConfigCodec_decodeResponse() {
        int fileClientMessageIndex = 734;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        DynamicConfigAddScheduledExecutorConfigCodec.ResponseParameters parameters = DynamicConfigAddScheduledExecutorConfigCodec.decodeResponse(fromFile);
    }

    @Test
    public void test_DynamicConfigAddQueueConfigCodec_encodeRequest() {
        int fileClientMessageIndex = 735;
        ClientMessage encoded = DynamicConfigAddQueueConfigCodec.encodeRequest(aString, aListOfListenerConfigHolders, anInt, anInt, anInt, anInt, aBoolean, aString, aQueueStoreConfigHolder, aString, anInt);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_DynamicConfigAddQueueConfigCodec_decodeResponse() {
        int fileClientMessageIndex = 736;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        DynamicConfigAddQueueConfigCodec.ResponseParameters parameters = DynamicConfigAddQueueConfigCodec.decodeResponse(fromFile);
    }

    @Test
    public void test_DynamicConfigAddMapConfigCodec_encodeRequest() {
        int fileClientMessageIndex = 737;
        ClientMessage encoded = DynamicConfigAddMapConfigCodec.encodeRequest(aString, anInt, anInt, anInt, anInt, anEvictionConfigHolder, aBoolean, aString, aString, anInt, aString, aListOfListenerConfigHolders, aListOfListenerConfigHolders, aBoolean, aString, aMapStoreConfigHolder, aNearCacheConfigHolder, aWanReplicationRef, aListOfIndexConfigs, aListOfAttributeConfigs, aListOfQueryCacheConfigHolders, aString, aData, aHotRestartConfig, anEventJournalConfig, aMerkleTreeConfig, anInt);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_DynamicConfigAddMapConfigCodec_decodeResponse() {
        int fileClientMessageIndex = 738;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        DynamicConfigAddMapConfigCodec.ResponseParameters parameters = DynamicConfigAddMapConfigCodec.decodeResponse(fromFile);
    }

    @Test
    public void test_DynamicConfigAddReliableTopicConfigCodec_encodeRequest() {
        int fileClientMessageIndex = 739;
        ClientMessage encoded = DynamicConfigAddReliableTopicConfigCodec.encodeRequest(aString, aListOfListenerConfigHolders, anInt, aBoolean, aString, aData);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_DynamicConfigAddReliableTopicConfigCodec_decodeResponse() {
        int fileClientMessageIndex = 740;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        DynamicConfigAddReliableTopicConfigCodec.ResponseParameters parameters = DynamicConfigAddReliableTopicConfigCodec.decodeResponse(fromFile);
    }

    @Test
    public void test_DynamicConfigAddCacheConfigCodec_encodeRequest() {
        int fileClientMessageIndex = 741;
        ClientMessage encoded = DynamicConfigAddCacheConfigCodec.encodeRequest(aString, aString, aString, aBoolean, aBoolean, aBoolean, aBoolean, aString, aString, aString, aString, anInt, anInt, aString, aString, aString, anInt, aBoolean, aListOfListenerConfigHolders, aString, aTimedExpiryPolicyFactoryConfig, aListOfCacheSimpleEntryListenerConfigs, anEvictionConfigHolder, aWanReplicationRef, anEventJournalConfig, aHotRestartConfig);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_DynamicConfigAddCacheConfigCodec_decodeResponse() {
        int fileClientMessageIndex = 742;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        DynamicConfigAddCacheConfigCodec.ResponseParameters parameters = DynamicConfigAddCacheConfigCodec.decodeResponse(fromFile);
    }

    @Test
    public void test_DynamicConfigAddFlakeIdGeneratorConfigCodec_encodeRequest() {
        int fileClientMessageIndex = 743;
        ClientMessage encoded = DynamicConfigAddFlakeIdGeneratorConfigCodec.encodeRequest(aString, anInt, aLong, aLong, aBoolean, aLong);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_DynamicConfigAddFlakeIdGeneratorConfigCodec_decodeResponse() {
        int fileClientMessageIndex = 744;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        DynamicConfigAddFlakeIdGeneratorConfigCodec.ResponseParameters parameters = DynamicConfigAddFlakeIdGeneratorConfigCodec.decodeResponse(fromFile);
    }

    @Test
    public void test_DynamicConfigAddPNCounterConfigCodec_encodeRequest() {
        int fileClientMessageIndex = 745;
        ClientMessage encoded = DynamicConfigAddPNCounterConfigCodec.encodeRequest(aString, anInt, aBoolean, aString);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_DynamicConfigAddPNCounterConfigCodec_decodeResponse() {
        int fileClientMessageIndex = 746;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        DynamicConfigAddPNCounterConfigCodec.ResponseParameters parameters = DynamicConfigAddPNCounterConfigCodec.decodeResponse(fromFile);
    }

    @Test
    public void test_FlakeIdGeneratorNewIdBatchCodec_encodeRequest() {
        int fileClientMessageIndex = 747;
        ClientMessage encoded = FlakeIdGeneratorNewIdBatchCodec.encodeRequest(aString, anInt);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_FlakeIdGeneratorNewIdBatchCodec_decodeResponse() {
        int fileClientMessageIndex = 748;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        FlakeIdGeneratorNewIdBatchCodec.ResponseParameters parameters = FlakeIdGeneratorNewIdBatchCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aLong, parameters.base));
        assertTrue(isEqual(aLong, parameters.increment));
        assertTrue(isEqual(anInt, parameters.batchSize));
    }

    @Test
    public void test_PNCounterGetCodec_encodeRequest() {
        int fileClientMessageIndex = 749;
        ClientMessage encoded = PNCounterGetCodec.encodeRequest(aString, aListOfUuidToLong, anAddress);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_PNCounterGetCodec_decodeResponse() {
        int fileClientMessageIndex = 750;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        PNCounterGetCodec.ResponseParameters parameters = PNCounterGetCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aLong, parameters.value));
        assertTrue(isEqual(aListOfUuidToLong, parameters.replicaTimestamps));
        assertTrue(isEqual(anInt, parameters.replicaCount));
    }

    @Test
    public void test_PNCounterAddCodec_encodeRequest() {
        int fileClientMessageIndex = 751;
        ClientMessage encoded = PNCounterAddCodec.encodeRequest(aString, aLong, aBoolean, aListOfUuidToLong, anAddress);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_PNCounterAddCodec_decodeResponse() {
        int fileClientMessageIndex = 752;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        PNCounterAddCodec.ResponseParameters parameters = PNCounterAddCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aLong, parameters.value));
        assertTrue(isEqual(aListOfUuidToLong, parameters.replicaTimestamps));
        assertTrue(isEqual(anInt, parameters.replicaCount));
    }

    @Test
    public void test_PNCounterGetConfiguredReplicaCountCodec_encodeRequest() {
        int fileClientMessageIndex = 753;
        ClientMessage encoded = PNCounterGetConfiguredReplicaCountCodec.encodeRequest(aString);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_PNCounterGetConfiguredReplicaCountCodec_decodeResponse() {
        int fileClientMessageIndex = 754;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        PNCounterGetConfiguredReplicaCountCodec.ResponseParameters parameters = PNCounterGetConfiguredReplicaCountCodec.decodeResponse(fromFile);
        assertTrue(isEqual(anInt, parameters.response));
    }

    @Test
    public void test_CPGroupCreateCPGroupCodec_encodeRequest() {
        int fileClientMessageIndex = 755;
        ClientMessage encoded = CPGroupCreateCPGroupCodec.encodeRequest(aString);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_CPGroupCreateCPGroupCodec_decodeResponse() {
        int fileClientMessageIndex = 756;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        CPGroupCreateCPGroupCodec.ResponseParameters parameters = CPGroupCreateCPGroupCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aRaftGroupId, parameters.groupId));
    }

    @Test
    public void test_CPGroupDestroyCPObjectCodec_encodeRequest() {
        int fileClientMessageIndex = 757;
        ClientMessage encoded = CPGroupDestroyCPObjectCodec.encodeRequest(aRaftGroupId, aString, aString);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_CPGroupDestroyCPObjectCodec_decodeResponse() {
        int fileClientMessageIndex = 758;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        CPGroupDestroyCPObjectCodec.ResponseParameters parameters = CPGroupDestroyCPObjectCodec.decodeResponse(fromFile);
    }

    @Test
    public void test_CPSessionCreateSessionCodec_encodeRequest() {
        int fileClientMessageIndex = 759;
        ClientMessage encoded = CPSessionCreateSessionCodec.encodeRequest(aRaftGroupId, aString);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_CPSessionCreateSessionCodec_decodeResponse() {
        int fileClientMessageIndex = 760;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        CPSessionCreateSessionCodec.ResponseParameters parameters = CPSessionCreateSessionCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aLong, parameters.sessionId));
        assertTrue(isEqual(aLong, parameters.ttlMillis));
        assertTrue(isEqual(aLong, parameters.heartbeatMillis));
    }

    @Test
    public void test_CPSessionCloseSessionCodec_encodeRequest() {
        int fileClientMessageIndex = 761;
        ClientMessage encoded = CPSessionCloseSessionCodec.encodeRequest(aRaftGroupId, aLong);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_CPSessionCloseSessionCodec_decodeResponse() {
        int fileClientMessageIndex = 762;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        CPSessionCloseSessionCodec.ResponseParameters parameters = CPSessionCloseSessionCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aBoolean, parameters.response));
    }

    @Test
    public void test_CPSessionHeartbeatSessionCodec_encodeRequest() {
        int fileClientMessageIndex = 763;
        ClientMessage encoded = CPSessionHeartbeatSessionCodec.encodeRequest(aRaftGroupId, aLong);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_CPSessionHeartbeatSessionCodec_decodeResponse() {
        int fileClientMessageIndex = 764;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        CPSessionHeartbeatSessionCodec.ResponseParameters parameters = CPSessionHeartbeatSessionCodec.decodeResponse(fromFile);
    }

    @Test
    public void test_CPSessionGenerateThreadIdCodec_encodeRequest() {
        int fileClientMessageIndex = 765;
        ClientMessage encoded = CPSessionGenerateThreadIdCodec.encodeRequest(aRaftGroupId);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_CPSessionGenerateThreadIdCodec_decodeResponse() {
        int fileClientMessageIndex = 766;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        CPSessionGenerateThreadIdCodec.ResponseParameters parameters = CPSessionGenerateThreadIdCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aLong, parameters.response));
    }

    @Test
    public void test_MCReadMetricsCodec_encodeRequest() {
        int fileClientMessageIndex = 767;
        ClientMessage encoded = MCReadMetricsCodec.encodeRequest(aUUID, aLong);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_MCReadMetricsCodec_decodeResponse() {
        int fileClientMessageIndex = 768;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MCReadMetricsCodec.ResponseParameters parameters = MCReadMetricsCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aListOfLongToByteArray, parameters.elements));
        assertTrue(isEqual(aLong, parameters.nextSequence));
    }

    @Test
    public void test_MCChangeClusterStateCodec_encodeRequest() {
        int fileClientMessageIndex = 769;
        ClientMessage encoded = MCChangeClusterStateCodec.encodeRequest(anInt);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_MCChangeClusterStateCodec_decodeResponse() {
        int fileClientMessageIndex = 770;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MCChangeClusterStateCodec.ResponseParameters parameters = MCChangeClusterStateCodec.decodeResponse(fromFile);
    }

    @Test
    public void test_MCGetMapConfigCodec_encodeRequest() {
        int fileClientMessageIndex = 771;
        ClientMessage encoded = MCGetMapConfigCodec.encodeRequest(aString);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_MCGetMapConfigCodec_decodeResponse() {
        int fileClientMessageIndex = 772;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MCGetMapConfigCodec.ResponseParameters parameters = MCGetMapConfigCodec.decodeResponse(fromFile);
        assertTrue(isEqual(anInt, parameters.inMemoryFormat));
        assertTrue(isEqual(anInt, parameters.backupCount));
        assertTrue(isEqual(anInt, parameters.asyncBackupCount));
        assertTrue(isEqual(anInt, parameters.timeToLiveSeconds));
        assertTrue(isEqual(anInt, parameters.maxIdleSeconds));
        assertTrue(isEqual(anInt, parameters.maxSize));
        assertTrue(isEqual(anInt, parameters.maxSizePolicy));
        assertTrue(isEqual(aBoolean, parameters.readBackupData));
        assertTrue(isEqual(anInt, parameters.evictionPolicy));
        assertTrue(isEqual(aString, parameters.mergePolicy));
    }

    @Test
    public void test_MCUpdateMapConfigCodec_encodeRequest() {
        int fileClientMessageIndex = 773;
        ClientMessage encoded = MCUpdateMapConfigCodec.encodeRequest(aString, anInt, anInt, anInt, aBoolean, anInt, anInt);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_MCUpdateMapConfigCodec_decodeResponse() {
        int fileClientMessageIndex = 774;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MCUpdateMapConfigCodec.ResponseParameters parameters = MCUpdateMapConfigCodec.decodeResponse(fromFile);
    }

    @Test
    public void test_MCGetMemberConfigCodec_encodeRequest() {
        int fileClientMessageIndex = 775;
        ClientMessage encoded = MCGetMemberConfigCodec.encodeRequest();
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_MCGetMemberConfigCodec_decodeResponse() {
        int fileClientMessageIndex = 776;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MCGetMemberConfigCodec.ResponseParameters parameters = MCGetMemberConfigCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aString, parameters.configXml));
    }

    @Test
    public void test_MCRunGcCodec_encodeRequest() {
        int fileClientMessageIndex = 777;
        ClientMessage encoded = MCRunGcCodec.encodeRequest();
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_MCRunGcCodec_decodeResponse() {
        int fileClientMessageIndex = 778;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MCRunGcCodec.ResponseParameters parameters = MCRunGcCodec.decodeResponse(fromFile);
    }

    @Test
    public void test_MCGetThreadDumpCodec_encodeRequest() {
        int fileClientMessageIndex = 779;
        ClientMessage encoded = MCGetThreadDumpCodec.encodeRequest(aBoolean);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_MCGetThreadDumpCodec_decodeResponse() {
        int fileClientMessageIndex = 780;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MCGetThreadDumpCodec.ResponseParameters parameters = MCGetThreadDumpCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aString, parameters.threadDump));
    }

    @Test
    public void test_MCShutdownMemberCodec_encodeRequest() {
        int fileClientMessageIndex = 781;
        ClientMessage encoded = MCShutdownMemberCodec.encodeRequest();
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_MCShutdownMemberCodec_decodeResponse() {
        int fileClientMessageIndex = 782;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MCShutdownMemberCodec.ResponseParameters parameters = MCShutdownMemberCodec.decodeResponse(fromFile);
    }

    @Test
    public void test_MCPromoteLiteMemberCodec_encodeRequest() {
        int fileClientMessageIndex = 783;
        ClientMessage encoded = MCPromoteLiteMemberCodec.encodeRequest();
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_MCPromoteLiteMemberCodec_decodeResponse() {
        int fileClientMessageIndex = 784;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MCPromoteLiteMemberCodec.ResponseParameters parameters = MCPromoteLiteMemberCodec.decodeResponse(fromFile);
    }

    @Test
    public void test_MCGetSystemPropertiesCodec_encodeRequest() {
        int fileClientMessageIndex = 785;
        ClientMessage encoded = MCGetSystemPropertiesCodec.encodeRequest();
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_MCGetSystemPropertiesCodec_decodeResponse() {
        int fileClientMessageIndex = 786;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MCGetSystemPropertiesCodec.ResponseParameters parameters = MCGetSystemPropertiesCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aListOfStringToString, parameters.systemProperties));
    }

    @Test
    public void test_MCGetTimedMemberStateCodec_encodeRequest() {
        int fileClientMessageIndex = 787;
        ClientMessage encoded = MCGetTimedMemberStateCodec.encodeRequest();
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_MCGetTimedMemberStateCodec_decodeResponse() {
        int fileClientMessageIndex = 788;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MCGetTimedMemberStateCodec.ResponseParameters parameters = MCGetTimedMemberStateCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aString, parameters.timedMemberStateJson));
    }

    @Test
    public void test_MCMatchMCConfigCodec_encodeRequest() {
        int fileClientMessageIndex = 789;
        ClientMessage encoded = MCMatchMCConfigCodec.encodeRequest(aString);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_MCMatchMCConfigCodec_decodeResponse() {
        int fileClientMessageIndex = 790;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MCMatchMCConfigCodec.ResponseParameters parameters = MCMatchMCConfigCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aBoolean, parameters.response));
    }

    @Test
    public void test_MCApplyMCConfigCodec_encodeRequest() {
        int fileClientMessageIndex = 791;
        ClientMessage encoded = MCApplyMCConfigCodec.encodeRequest(aString, anInt, aListOfClientBwListEntries);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_MCApplyMCConfigCodec_decodeResponse() {
        int fileClientMessageIndex = 792;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MCApplyMCConfigCodec.ResponseParameters parameters = MCApplyMCConfigCodec.decodeResponse(fromFile);
    }

    @Test
    public void test_MCGetClusterMetadataCodec_encodeRequest() {
        int fileClientMessageIndex = 793;
        ClientMessage encoded = MCGetClusterMetadataCodec.encodeRequest();
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_MCGetClusterMetadataCodec_decodeResponse() {
        int fileClientMessageIndex = 794;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MCGetClusterMetadataCodec.ResponseParameters parameters = MCGetClusterMetadataCodec.decodeResponse(fromFile);
        assertTrue(isEqual(aByte, parameters.currentState));
        assertTrue(isEqual(aString, parameters.memberVersion));
        assertTrue(isEqual(aString, parameters.jetVersion));
        assertTrue(isEqual(aLong, parameters.clusterTime));
    }

    @Test
    public void test_MCShutdownClusterCodec_encodeRequest() {
        int fileClientMessageIndex = 795;
        ClientMessage encoded = MCShutdownClusterCodec.encodeRequest();
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_MCShutdownClusterCodec_decodeResponse() {
        int fileClientMessageIndex = 796;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MCShutdownClusterCodec.ResponseParameters parameters = MCShutdownClusterCodec.decodeResponse(fromFile);
    }

    @Test
    public void test_MCChangeClusterVersionCodec_encodeRequest() {
        int fileClientMessageIndex = 797;
        ClientMessage encoded = MCChangeClusterVersionCodec.encodeRequest(aByte, aByte);
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        compareClientMessages(fromFile, encoded);
    }

    @Test
    public void test_MCChangeClusterVersionCodec_decodeResponse() {
        int fileClientMessageIndex = 798;
        ClientMessage fromFile = clientMessages.get(fileClientMessageIndex);
        MCChangeClusterVersionCodec.ResponseParameters parameters = MCChangeClusterVersionCodec.decodeResponse(fromFile);
    }

    private void compareClientMessages(ClientMessage binaryMessage, ClientMessage encodedMessage) {
        ClientMessage.Frame binaryFrame, encodedFrame;

        ClientMessage.ForwardFrameIterator binaryFrameIterator = binaryMessage.frameIterator();
        ClientMessage.ForwardFrameIterator encodedFrameIterator = encodedMessage.frameIterator();

        boolean isInitialFramesCompared = false;
        while (binaryFrameIterator.hasNext()) {
            binaryFrame = binaryFrameIterator.next();
            encodedFrame = encodedFrameIterator.next();
            assertNotNull("Encoded client message has less frames.", encodedFrame);

            boolean isFinal = binaryFrameIterator.peekNext() == null;
            if (!isInitialFramesCompared) {
                compareInitialFrame(binaryFrame, encodedFrame, isFinal);
                isInitialFramesCompared = true;
            } else {
                assertArrayEquals("Frames have different contents", binaryFrame.content, encodedFrame.content);
                int flags = isFinal ? encodedFrame.flags | IS_FINAL_FLAG : encodedFrame.flags;
                assertEquals("Frames have different flags", binaryFrame.flags, flags);
            }
        }
        assertTrue("Client message that is read from the binary file does not have any frames", isInitialFramesCompared);
    }

    private void compareInitialFrame(ClientMessage.Frame binaryFrame, ClientMessage.Frame encodedFrame, boolean isFinal) {
        assertTrue("Encoded client message have shorter initial frame",
                binaryFrame.content.length <= encodedFrame.content.length);
        assertArrayEquals("Initial frames have different contents",
                binaryFrame.content, Arrays.copyOf(encodedFrame.content, binaryFrame.content.length));
        int flags = isFinal ? encodedFrame.flags | IS_FINAL_FLAG : encodedFrame.flags;
        assertEquals("Initial frames have different flags", binaryFrame.flags, flags);
    }
}