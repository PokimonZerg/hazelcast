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

package com.hazelcast.client.impl.protocol.codec;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.Generated;
import com.hazelcast.client.impl.protocol.codec.builtin.*;
import com.hazelcast.client.impl.protocol.codec.custom.*;

import javax.annotation.Nullable;

import static com.hazelcast.client.impl.protocol.ClientMessage.*;
import static com.hazelcast.client.impl.protocol.codec.builtin.FixedSizeTypesCodec.*;

/*
 * This file is auto-generated by the Hazelcast Client Protocol Code Generator.
 * To change this file, edit the templates or the protocol
 * definitions on the https://github.com/hazelcast/hazelcast-client-protocol
 * and regenerate it.
 */

/**
 * Submits the task to partition for execution, partition is chosen based on multiple criteria of the given task.
 */
@Generated("9846304f3f92cfdf8ce734b3bb50b1b3")
public final class ScheduledExecutorSubmitToPartitionCodec {
    //hex: 0x1A0200
    public static final int REQUEST_MESSAGE_TYPE = 1704448;
    //hex: 0x1A0201
    public static final int RESPONSE_MESSAGE_TYPE = 1704449;
    private static final int REQUEST_TYPE_FIELD_OFFSET = PARTITION_ID_FIELD_OFFSET + INT_SIZE_IN_BYTES;
    private static final int REQUEST_INITIAL_DELAY_IN_MILLIS_FIELD_OFFSET = REQUEST_TYPE_FIELD_OFFSET + BYTE_SIZE_IN_BYTES;
    private static final int REQUEST_PERIOD_IN_MILLIS_FIELD_OFFSET = REQUEST_INITIAL_DELAY_IN_MILLIS_FIELD_OFFSET + LONG_SIZE_IN_BYTES;
    private static final int REQUEST_INITIAL_FRAME_SIZE = REQUEST_PERIOD_IN_MILLIS_FIELD_OFFSET + LONG_SIZE_IN_BYTES;
    private static final int RESPONSE_INITIAL_FRAME_SIZE = RESPONSE_BACKUP_ACKS_FIELD_OFFSET + INT_SIZE_IN_BYTES;

    private ScheduledExecutorSubmitToPartitionCodec() {
    }

    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings({"URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD"})
    public static class RequestParameters {

        /**
         * The name of the scheduler.
         */
        public java.lang.String schedulerName;

        /**
         * type of schedule logic, values 0 for SINGLE_RUN, 1 for AT_FIXED_RATE
         */
        public byte type;

        /**
         * The name of the task
         */
        public java.lang.String taskName;

        /**
         * Name The name of the task
         */
        public com.hazelcast.nio.serialization.Data task;

        /**
         * initial delay in milliseconds
         */
        public long initialDelayInMillis;

        /**
         * period between each run in milliseconds
         */
        public long periodInMillis;
    }

    public static ClientMessage encodeRequest(java.lang.String schedulerName, byte type, java.lang.String taskName, com.hazelcast.nio.serialization.Data task, long initialDelayInMillis, long periodInMillis) {
        ClientMessage clientMessage = ClientMessage.createForEncode();
        clientMessage.setRetryable(true);
        clientMessage.setOperationName("ScheduledExecutor.SubmitToPartition");
        ClientMessage.Frame initialFrame = new ClientMessage.Frame(new byte[REQUEST_INITIAL_FRAME_SIZE], UNFRAGMENTED_MESSAGE);
        encodeInt(initialFrame.content, TYPE_FIELD_OFFSET, REQUEST_MESSAGE_TYPE);
        encodeByte(initialFrame.content, REQUEST_TYPE_FIELD_OFFSET, type);
        encodeLong(initialFrame.content, REQUEST_INITIAL_DELAY_IN_MILLIS_FIELD_OFFSET, initialDelayInMillis);
        encodeLong(initialFrame.content, REQUEST_PERIOD_IN_MILLIS_FIELD_OFFSET, periodInMillis);
        clientMessage.add(initialFrame);
        StringCodec.encode(clientMessage, schedulerName);
        StringCodec.encode(clientMessage, taskName);
        DataCodec.encode(clientMessage, task);
        return clientMessage;
    }

    public static ScheduledExecutorSubmitToPartitionCodec.RequestParameters decodeRequest(ClientMessage clientMessage) {
        ClientMessage.ForwardFrameIterator iterator = clientMessage.frameIterator();
        RequestParameters request = new RequestParameters();
        ClientMessage.Frame initialFrame = iterator.next();
        request.type = decodeByte(initialFrame.content, REQUEST_TYPE_FIELD_OFFSET);
        request.initialDelayInMillis = decodeLong(initialFrame.content, REQUEST_INITIAL_DELAY_IN_MILLIS_FIELD_OFFSET);
        request.periodInMillis = decodeLong(initialFrame.content, REQUEST_PERIOD_IN_MILLIS_FIELD_OFFSET);
        request.schedulerName = StringCodec.decode(iterator);
        request.taskName = StringCodec.decode(iterator);
        request.task = DataCodec.decode(iterator);
        return request;
    }

    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings({"URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD"})
    public static class ResponseParameters {
    }

    public static ClientMessage encodeResponse() {
        ClientMessage clientMessage = ClientMessage.createForEncode();
        ClientMessage.Frame initialFrame = new ClientMessage.Frame(new byte[RESPONSE_INITIAL_FRAME_SIZE], UNFRAGMENTED_MESSAGE);
        encodeInt(initialFrame.content, TYPE_FIELD_OFFSET, RESPONSE_MESSAGE_TYPE);
        clientMessage.add(initialFrame);

        return clientMessage;
    }

    public static ScheduledExecutorSubmitToPartitionCodec.ResponseParameters decodeResponse(ClientMessage clientMessage) {
        ClientMessage.ForwardFrameIterator iterator = clientMessage.frameIterator();
        ResponseParameters response = new ResponseParameters();
        //empty initial frame
        iterator.next();
        return response;
    }

}
