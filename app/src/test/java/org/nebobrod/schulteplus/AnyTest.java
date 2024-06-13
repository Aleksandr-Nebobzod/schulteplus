/*
 * Copyright (c) 2024  "Smart Rovers"
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package org.nebobrod.schulteplus;

import static org.junit.Assert.assertTrue;
import static org.nebobrod.schulteplus.Utils.timeStampFormattedShortUtc;

import org.junit.Test;

import java.time.Instant;

public class AnyTest {

	/**
	 * We put here any tests and then close it with comment
	 */
	@Test
	public void runTest () {

		// String convert
/*		String documentName = "123123.qweqwe";
		documentName = String.valueOf(123.456789d);

		documentName = documentName.substring(0, documentName.indexOf("."));

		// and different, new:
		int dotIndex = documentName.indexOf(".");
		if (dotIndex >= 0) {
			_documentName = documentName.substring(dotIndex); 	// From the dot till end of string
		}

		System.out.println("RESULT: " + documentName);*/

		// Convert int to Hex String
/*		int value = -1878724255; //-13516; //

		String hexString = String.format("%08X", value);
		System.out.println("1: " + hexString);

		// this is wrong for an int
		int value2 = (value < 0) ? value + 65536 : value;
		System.out.println("2: " + Integer.toString(value2, 16));

		// Correct way to handle negative values of int is adding 2^32 (4294967296)
		long unsignedValue = value & 0xFFFFFFFFL;
		System.out.println("3: " + Long.toString(unsignedValue, 16));*/


		// Time
		Instant instant = Instant.now();
		long ts = instant.getEpochSecond();
		System.out.println("0: " + instant);
		System.out.println("1: " + ts);
		System.out.println("2: " + System.nanoTime());
		System.out.println("3: " + Instant.ofEpochSecond(ts).toString());


		// Next






		assertTrue(true);
	}
}
