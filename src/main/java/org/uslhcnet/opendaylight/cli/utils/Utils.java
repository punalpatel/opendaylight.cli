package org.uslhcnet.opendaylight.cli.utils;

/*
 * Copyright (c) 2014, California Institute of Technology
 * ALL RIGHTS RESERVED.
 * Based on Government Sponsored Research DE-SC0007346
 * Author Michael Bredel <michael.bredel@cern.ch>
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS
 * OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED
 * AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
 * WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * Neither the name of the California Institute of Technology
 * (Caltech) nor the names of its contributors may be used to endorse
 * or promote products derived from this software without specific prior
 * written permission.
 */

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Michael Bredel <michael.bredel@cern.ch>
 */
public class Utils {

    /**
     * Parses a date and returns a formated date string in the form:
     * "yyyy-MM-dd HH:mm:ss z", where z is the time zone.
     *
     * @param timestamp
     *            The Unix timestamp that needs to be converted into a formated
     *            string.
     * @return <b>String</b> A formated date string.
     */
    public static String parseDate(long timestamp) {
        Date date = new Date();
        date.setTime(timestamp);
        return parseDate(date);
    }

    /**
     * Parses a date and returns a formated date string in the form:
     * "yyyy-MM-dd HH:mm:ss z", where z is the time zone.
     *
     * @param date
     *            The date that needs to be converted into a formated string.
     * @return <b>String</b> A formated date string.
     */
    public static String parseDate(Date date) {
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
        return dateformat.format(date);
    }

    /**
     * Parses the capacity and returns a human readable string.
     *
     * @param capacity
     *            The capacity in [Mbps].
     * @return <b>String</b> The capacity in a human readable string
     *         representation.
     */
    public static String parseCapacity(int capacity) {
        /* States whether the link is full or half duplex. */
        boolean fullDuplex = true;
        /* The resulting string. */
        String result = "";

        if (capacity < 0) {
            capacity = capacity * (-1);
            fullDuplex = false;
        }

        switch (capacity) {
        case 10:
            result += "10 Mb/s";
            break;
        case 100:
            result += "100 Mb/s";
            break;
        case 1000:
            result += "1 Gb/s";
            break;
        case 10000:
            result += "10 Gb/s";
            break;
        case 40000:
            result += "40 Gb/s";
            break;
        case 100000:
            result += "100 Gb/s";
            break;
        default:
            if (capacity >= 1000) {
                result += String.valueOf(capacity / 1000) + " Gb/s";
            } else {
                result += String.valueOf(capacity) + " Mb/s";
            }
        }

        if (!fullDuplex)
            result += " HD";

        return result;
    }

    /**
     * Parses the bit rate and returns a human readable string.
     *
     *
     * @param bitRate
     *            The capacity in [bps].
     * @return <b>String</b> The bit rate in a human readable string
     *         representation.
     */
    public static String parseBitRate(long bitRate) {
        if (bitRate >= 1000 * 1000 * 1000) {
            return bitRate / 1000 / 1000 / 1000 + " Gb/s";
        }
        if (bitRate >= 1000 * 1000) {
            return bitRate / 1000 / 1000 + " Mb/s";
        }
        if (bitRate >= 1000) {
            return bitRate / 1000 + " Kb/s";
        } else {
            return bitRate + " b/s";
        }
    }

    /**
     * Private constructor for singleton pattern.
     */
    private Utils() {
        // Do nothing.
    }
}
