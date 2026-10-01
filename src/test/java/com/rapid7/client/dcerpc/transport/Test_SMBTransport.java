/**
 * Copyright 2017, Rapid7, Inc.
 *
 * License: BSD-3-clause
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of the copyright holder nor the names of its contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 */
package com.rapid7.client.dcerpc.transport;

import java.io.IOException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import com.rapid7.helper.smbj.share.NamedPipe;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class Test_SMBTransport {
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void transact() throws IOException {
        final NamedPipe namedPipe = mock(NamedPipe.class);
        when(namedPipe.transact(any(byte[].class))).thenReturn(new byte[]{1, 2, 3});
        final byte[] packetIn = new byte[3];

        assertEquals(3, new SMBTransport(namedPipe).transact(new byte[0], packetIn));
        assertArrayEquals(new byte[]{1, 2, 3}, packetIn);
    }

    @Test
    public void transactOversized() throws IOException {
        final NamedPipe namedPipe = mock(NamedPipe.class);
        when(namedPipe.transact(any(byte[].class))).thenReturn(new byte[5]);

        thrown.expect(IOException.class);
        thrown.expectMessage("Response packet length 5 exceeds receive buffer size 4.");
        new SMBTransport(namedPipe).transact(new byte[0], new byte[4]);
    }

    @Test
    public void read() throws IOException {
        final NamedPipe namedPipe = mock(NamedPipe.class);
        when(namedPipe.read()).thenReturn(new byte[]{1, 2});
        final byte[] packetIn = new byte[4];

        assertEquals(2, new SMBTransport(namedPipe).read(packetIn));
        assertArrayEquals(new byte[]{1, 2, 0, 0}, packetIn);
    }

    @Test
    public void readOversized() throws IOException {
        final NamedPipe namedPipe = mock(NamedPipe.class);
        when(namedPipe.read()).thenReturn(new byte[5]);

        thrown.expect(IOException.class);
        thrown.expectMessage("Response packet length 5 exceeds receive buffer size 4.");
        new SMBTransport(namedPipe).read(new byte[4]);
    }
}
