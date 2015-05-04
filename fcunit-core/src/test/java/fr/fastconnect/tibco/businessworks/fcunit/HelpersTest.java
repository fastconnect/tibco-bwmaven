/**
 *     Copyright (C) 2011 FastConnect SAS http://www.fastconnect.fr/
 *
 *     This file is part of FCUnit
 *
 *     FCUnit is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     FCUnit is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package fr.fastconnect.tibco.businessworks.fcunit;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

@RunWith(BlockJUnit4ClassRunner.class)
public class HelpersTest {

	@Test
	public void testMatchCase() {
		assertTrue(Helpers.matchCase("MonTestCase"));
		assertTrue(Helpers.matchCase("TestCase"));
		assertFalse(Helpers.matchCase("MontestCase")); // t, not T
		assertFalse(Helpers.matchCase("MonTestcase")); // c, not C
		assertFalse(Helpers.matchCase("MonTEstCase")); // E, not e
		assertFalse(Helpers.matchCase("UnTestCaseOuPas")); // Not end of string
		assertFalse(Helpers.matchCase("UneTestSuite")); // Suite, not Case
	}

	@Test
	public void testMatchSuite() {
		assertTrue(Helpers.matchSuite("UneTestSuite"));
		assertTrue(Helpers.matchSuite("TestSuite"));
		assertFalse(Helpers.matchSuite("MatestSuite")); // t, not T
		assertFalse(Helpers.matchSuite("MaTestsuite")); // c, not C
		assertFalse(Helpers.matchSuite("MaTEstSuite")); // E, not e
		assertFalse(Helpers.matchSuite("UneTestSuiteOuPas")); // Not end of string
		assertFalse(Helpers.matchSuite("UnTestCase")); // Case, not Suite
	}

}
