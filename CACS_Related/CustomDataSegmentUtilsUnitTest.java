/**
 * (c)1985-2016 CGI Technologies and Solutions Inc.   *  * This software and its related documentation are the proprietary and confidential  * material of CGI Technologies and Solutions Inc. [and its licensors] and is protected  * by United States and international copyright laws and international treaties. This  * software and its related documentation is made available under the terms of the CGI  * license agreement and may not be used, reproduced or disclosed in any manner except  * as expressly authorized by the license agreement-all other uses are strictly prohibited. *  * Printed in the U.S.A. *  * CGI Technologies and Solutions Inc. is ISO 9001 Certified [http://www.cgi.com/web/en/quality.htm] */

/*
 * Header Placeholder
 */
package test.com.cgi.ec.serverdependent.common.util;

import java.security.PrivilegedAction;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import test.com.cgi.eci.serverdependent.BaseServletTestCase;

import com.ams.core.momapi.xml.ObjectBean;
import com.ams.core.service.ServiceFactory;
import com.ams.core.util.DomainObjectFactory;
import com.ams.core.util.UpdateStatus;
import com.cgi.ec.common.util.CustomDataSegmentUtils;
import com.cgi.ec.common.util.ProblemNumbers;
import com.cgi.ec.common.util.StringUtils;
import com.cgi.ec.common.util.fixedlengthrecords.CustomDataSegmentRecord;
import com.cgi.ec.domain.caseprocessing.Account;
import com.cgi.ec.domain.caseprocessing.CustomDataSegment;
import com.cgi.ec.domain.caseprocessing.CustomDataValue;
import com.cgi.ec.domain.caseprocessing.CustomerPortfolio;
import com.cgi.ec.domain.caseprocessing.PortfolioCustomDataSegment;
import com.cgi.ec.domain.systemadmin.systemparameters.CustomDataDefinition;
import com.cgi.ec.domain.systemadmin.systemparameters.CustomDataDefinitionIdentity;
import com.cgi.ec.domain.systemadmin.systemparameters.CustomDataElementAssignment;
import com.cgi.ec.service.systemadmin.systemparameters.CustomDataDefinitionService;
import com.cgi.eci.common.util.ServiceUtils;


/**
 * Tests the CustomDataSegmentUtils class.
 */
public class CustomDataSegmentUtilsUnitTest extends BaseServletTestCase {
    /* first element length 40 */
	public static final String BPO_CDS_ADDRESS_DATA = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX";

	public static final String BPO_CDS_DATE_DATA = "YYYYMMDD";

	public static final String BPO_CDS_AMOUNT_DATA = "+123456789012123456";

	/**
	 * Required by JUnit.
	 *
	 * @param name
	 *            Name of the test method to execute.
	 */
	public CustomDataSegmentUtilsUnitTest(String name) {
		super(name);
	}

	/**
	 * Sets up the instance variable for use within the test class.
	 */
	public void setUp() {
		super.setUp();

	}

	/**
	 * Removes all the data created for this test.
	 */
	public void tearDown() {
		super.tearDown();
	}

	/**
	 * Test CustomDataSegmentUtils
	 */
	public void testCustomDataSegmentUtils() {
		doAsSubject(new PrivilegedAction() {
			public Object run() {
				try {
				  executeTestCustomDataSegmentUtils();
				} catch (ParseException ex) {
					return null;
				}
				return null;
			}
		}, "MATTCACS");
	}

	/**
	 * Test CustomDataSegmentUtils
	 */
	public void executeTestCustomDataSegmentUtils() throws ParseException {

		CustomDataSegmentUtils utils = CustomDataSegmentUtils.getInstance();
		//test the CustomDataSegmentRecordResolver
		CustomDataSegmentRecord fixedLengthRecord = utils.getCustomDataSegmentRecord("SGY");

         assertNotNull(fixedLengthRecord);
         assertNotNull(fixedLengthRecord.getFormatArray());
         String formatArray[][] = fixedLengthRecord.getFormatArray();
         //tests max length and format are set properly
         assertEquals(formatArray[0][0], "elementValue0");
         assertEquals(formatArray[0][1], "08");
         assertEquals(formatArray[0][2], CustomDataSegmentRecord.ALPHA);

         fixedLengthRecord = utils.getCustomDataSegmentRecord("ACL");
         assertNotNull(fixedLengthRecord);
         assertNotNull(fixedLengthRecord.getFormatArray());
         formatArray = fixedLengthRecord.getFormatArray();
       //tests max length and format are set properly for amount field
         assertEquals(formatArray[64][0], "elementValue64");
         assertEquals(formatArray[64][1], "19");
         assertEquals(formatArray[64][2], CustomDataSegmentRecord.SIGNED_DECIMAL6);

         //make sure we can load up the remaining baseline CDS segments
         //with completeInd set to true
         fixedLengthRecord = utils.getCustomDataSegmentRecord("ACH");
         assertNotNull(fixedLengthRecord);
         assertNotNull(fixedLengthRecord.getFormatArray());

         fixedLengthRecord = utils.getCustomDataSegmentRecord("ACT");
         assertNotNull(fixedLengthRecord);
         assertNotNull(fixedLengthRecord.getFormatArray());

         fixedLengthRecord = utils.getCustomDataSegmentRecord("ADD");
         assertNotNull(fixedLengthRecord);
         assertNotNull(fixedLengthRecord.getFormatArray());

         fixedLengthRecord = utils.getCustomDataSegmentRecord("BKO");
         assertNotNull(fixedLengthRecord);
         assertNotNull(fixedLengthRecord.getFormatArray());

         fixedLengthRecord = utils.getCustomDataSegmentRecord("BPO");
         assertNotNull(fixedLengthRecord);
         assertNotNull(fixedLengthRecord.getFormatArray());

         fixedLengthRecord = utils.getCustomDataSegmentRecord("JDG");
         assertNotNull(fixedLengthRecord);
         assertNotNull(fixedLengthRecord.getFormatArray());

         fixedLengthRecord = utils.getCustomDataSegmentRecord("PRB");
         assertNotNull(fixedLengthRecord);
         assertNotNull(fixedLengthRecord.getFormatArray());

         fixedLengthRecord = utils.getCustomDataSegmentRecord("SCN");
         assertNotNull(fixedLengthRecord);
         assertNotNull(fixedLengthRecord.getFormatArray());

         fixedLengthRecord = utils.getCustomDataSegmentRecord("SET");
         assertNotNull(fixedLengthRecord);
         assertNotNull(fixedLengthRecord.getFormatArray());

         fixedLengthRecord = utils.getCustomDataSegmentRecord("ENT");
         assertNotNull(fixedLengthRecord);
         assertNotNull(fixedLengthRecord.getFormatArray());

         fixedLengthRecord = utils.getCustomDataSegmentRecord("ENT");
         assertNotNull(fixedLengthRecord);
         assertNotNull(fixedLengthRecord.getFormatArray());

         //ALD is not marked complete and should be null
         fixedLengthRecord = utils.getCustomDataSegmentRecord("ALD");
         assertNull(fixedLengthRecord);
     }

	/**
	 * Test executeTestParseCustomDataSegmentFixedLengthRecord
	 */
	public void testParseCustomDataSegmentFixedLengthRecord() {
		doAsSubject(new PrivilegedAction() {
			public Object run() {
				try {
				  executeTestParseCustomDataSegmentFixedLengthRecord();
				} catch (ParseException ex) {
					return null;
				}
				return null;
			}
		}, "MATTCACS");
	}

	/**
	 * Test executeTestParseCustomDataSegmentFixedLengthRecord
	 */
	public void executeTestParseCustomDataSegmentFixedLengthRecord() throws ParseException {

		CustomDataSegment customDataSegment = new CustomDataSegment();
		String customDataSegmentData = BPO_CDS_ADDRESS_DATA + BPO_CDS_DATE_DATA + BPO_CDS_AMOUNT_DATA;
		CustomDataSegmentUtils utils = CustomDataSegmentUtils.getInstance();
		//test the CustomDataSegmentRecordResolver
		List <CustomDataValue> customDataList =
 customDataSegment
				.parseCustomDataSegmentFixedLengthRecord("BPO",
						customDataSegmentData);
		//11 custom data elements
		assertEquals(11, customDataList.size());

		//BPO Address 6000 should be first in list
		CustomDataValue customDataValue = customDataList.get(0);
		assertEquals ("6000", customDataValue.getElementId());
		assertTrue (BPO_CDS_ADDRESS_DATA.equals(customDataValue.getDataValue()));

		//BPO Date 6001 should be second in list
		customDataValue = customDataList.get(1);
		assertEquals ("6001", customDataValue.getElementId());
		assertTrue (BPO_CDS_DATE_DATA.equals(customDataValue.getDataValue()));

		//BPO Amount 6002 should be third in list
		customDataValue = customDataList.get(2);
		assertEquals ("6002", customDataValue.getElementId());
		assertEquals ("+123456789012.123456",customDataValue.getDataValue().trim());

		//now test an update
		customDataValue.setElementId("6002");
		customDataValue.setDataValue("+000000999999.999999");
	    List<CustomDataValue> customDataValues = new ArrayList<CustomDataValue>();
	    customDataValues.add(customDataValue);

	    String updatedSegmentData = utils.updateCustomDataSegmentFixedLengthRecord("BPO",
	    		customDataSegmentData, customDataValues);

	    customDataList =
 customDataSegment
				.parseCustomDataSegmentFixedLengthRecord("BPO",
						updatedSegmentData);

		//BPO Amount 6002 should be third in list
		customDataValue = customDataList.get(2);
		assertEquals ("6002", customDataValue.getElementId());
		assertEquals ("+000000999999.999999", customDataValue.getDataValue().trim());

		//now test an update of a negative amount
		customDataValue.setElementId("6002");
		customDataValue.setDataValue("-000008888888.888888");
	    customDataValues = new ArrayList<CustomDataValue>();
	    customDataValues.add(customDataValue);

	   updatedSegmentData = utils.updateCustomDataSegmentFixedLengthRecord("BPO",
	    		customDataSegmentData, customDataValues);

	    customDataList =
 customDataSegment
				.parseCustomDataSegmentFixedLengthRecord("BPO",
						updatedSegmentData);

		//BPO Amount 6002 should be third in list
		customDataValue = customDataList.get(2);
		assertEquals ("6002", customDataValue.getElementId());
		assertEquals ("-000008888888.888888", customDataValue.getDataValue().trim());

		//make sure zero padding works
		customDataValue.setElementId("6002");
		customDataValue.setDataValue("+100.00");
	    customDataValues = new ArrayList<CustomDataValue>();
	    customDataValues.add(customDataValue);

	   updatedSegmentData = utils.updateCustomDataSegmentFixedLengthRecord("BPO",
	    		customDataSegmentData, customDataValues);

	    customDataList =
 customDataSegment
				.parseCustomDataSegmentFixedLengthRecord("BPO",
						updatedSegmentData);

		//BPO Amount 6002 should be third in list
		customDataValue = customDataList.get(2);
		assertEquals ("6002", customDataValue.getElementId());
		assertEquals ("+000000000100.000000", customDataValue.getDataValue().trim());
	}

	/**
	 * Test executeTestCustomDataSegmentValidator
	 */
	public void testCustomDataSegmentUtilsFormatting() {
		doAsSubject(new PrivilegedAction() {
			public Object run() {

				  executeTestCustomDataSegmentUtilsFormatting();

				return null;
			}
		}, "MATTCACS");
	}
	/**
	 * Test executeTestParseCustomDataSegmentFixedLengthRecord
	 */
	public void executeTestCustomDataSegmentUtilsFormatting()  {

		CustomDataSegmentUtils utils = CustomDataSegmentUtils.getInstance();
		String originalValue = "AAA";
		String newValue = utils.checkValueForFormatting("A", originalValue);
		//no formatting change for ALPHA
		assertEquals(originalValue, newValue);

		originalValue = "12345";
		newValue = utils.checkValueForFormatting("N", originalValue);
		//unsigned integers should be positive
		assertTrue("positive sign should make them unequal ", !(originalValue.equals(newValue)));
		assertEquals("+", StringUtils.getInstance().subString(newValue,0,1));

		originalValue = "123.45";
		newValue = utils.checkValueForFormatting("3", originalValue);
		//unsigned decimals should be positive
		assertTrue("positive sign should make them unequal ", !(originalValue.equals(newValue)));
		assertEquals("+", StringUtils.getInstance().subString(newValue,0,1));

		originalValue = "+123.45";
		newValue = utils.checkValueForFormatting("3", originalValue);
		//already signed decimals should remain unchanged
		assertEquals(originalValue, newValue);

		originalValue = "-123.45";
		newValue = utils.checkValueForFormatting("3", originalValue);
		//already signed decimals should remain unchanged
		assertEquals(originalValue, newValue);

	}

	/**
	 * Test executeTestCustomDataSegmentValidator
	 */
	public void testCustomDataSegmentValidator() {
		doAsSubject(new PrivilegedAction() {
			public Object run() {

				  executeTestCustomDataSegmentValidator();

				return null;
			}
		}, "MATTCACS");
	}

	/**
	 * Test executeTestParseCustomDataSegmentFixedLengthRecord
	 */
	public void executeTestCustomDataSegmentValidator()  {
		Account account = (Account)DomainObjectFactory.getInstance().
	      newObject(Account.CLASS_ID);
	    account.setLocationCode("201112");
	    account.setAccountNumber("0303");
	    account = (Account)ServiceUtils.loadObjectByBK((ObjectBean)account, true);
	    CustomDataSegment cds = (CustomDataSegment)DomainObjectFactory.getInstance().
	          newObject(CustomDataSegment.CLASS_ID);
	    cds.setUpdateStatus(UpdateStatus.ADD);
	    cds.setSegmentID("BPO");
	    cds.setCustomDataSegmentData("");
	    account.addChild(cds);

	    account = (Account)ServiceUtils.changeObjectBean((ObjectBean)account);

	    assertListContainsSpecifiedProblem(account, ProblemNumbers.CDS_REQUIRED_FIELD_MISSING);

	    account = (Account)ServiceUtils.loadObjectByBK((ObjectBean)account, true);

	    CustomDataSegmentUtils utils = CustomDataSegmentUtils.getInstance();
        CustomDataValue element1 = new CustomDataValue();
        element1.setElementId("6000");
        element1.setDataValue(BPO_CDS_ADDRESS_DATA);
        cds.addChild(element1);

        CustomDataValue element2 = new CustomDataValue();
        element2.setElementId("6001");
        element2.setDataValue("baddate");
        cds.addChild(element2);

        CustomDataValue element3 = new CustomDataValue();
        element3.setElementId("6002");
        element3.setDataValue("badamount");
        cds.addChild(element3);

	    String segmentData = utils.updateCustomDataSegmentFixedLengthRecord("BPO", "",
	    		cds.getCustomDataValues());
	    cds.setCustomDataSegmentData(segmentData);
	    account.addChild(cds);
        account = (Account)ServiceUtils.changeObjectBean((ObjectBean)account);

	    assertListContainsSpecifiedProblem(account, "10016");

	    assertListContainsSpecifiedProblem(account, ProblemNumbers.CDS_INVALID_FORMAT);
	}

	public void testAddBaselineElementToSegment() {
		doAsSubject(new PrivilegedAction() {
			public Object run() {
				executeTestAddBaselineElementToSegment();

				return null;
			}
		}, "MATTCACS");
	}

	/**
	 * Call test method to add a baseline element
	 */
	public void executeTestAddBaselineElementToSegment() {
		CustomDataSegmentUtils utils = CustomDataSegmentUtils.getInstance();
		CustomDataDefinitionService service = (CustomDataDefinitionService) ServiceFactory
				.getInstance().lookup(CustomDataDefinition.CLASS_ID);

		CustomDataDefinition testCDS = new CustomDataDefinition();
		testCDS.setCustomDataSegmentID("SLE");

		String guid = ServiceUtils.lookupPKByBK(testCDS);

		assertNotNull("expecting the selected domain", guid);
		CustomDataDefinitionIdentity identity = new CustomDataDefinitionIdentity();
		identity.setGuid(guid);

		testCDS = service.select(identity, "select", Boolean.TRUE);
		assertNotNull("expecting the selected domain", testCDS);
		assertTrue(testCDS.getCustomDataElementAssignment().size() > 1);

		Integer preUpdateLength = testCDS.getDataSegmentLength();

		// adding a new baseline element should not increase the segment size
		testCDS.setElementData4("0955SNPLCA    NNNNN");
		testCDS.setDisplaySequenceNumber4(4);
		testCDS.setCompleteInd(true);
		testCDS = service.update(testCDS);
		assertNoProblems(testCDS);
		assertEquals("should not increase the segment size", preUpdateLength,
				testCDS.getDataSegmentLength());

		CustomDataSegmentRecord fixedLengthRecord = utils
				.getCustomDataSegmentRecord("SLE");
		assertNotNull(fixedLengthRecord);

		assertNotNull(fixedLengthRecord.getFormatArray());
		String[][] formatArray = fixedLengthRecord.getFormatArray();
		String length = (String) formatArray[3][1];

		assertEquals("baseline elements should be length 00", length, "00");
		CustomDataElementAssignment cde = (CustomDataElementAssignment) testCDS
				.getCustomDataElementAssignment().get(0);
		// first three elements are custom
		assertFalse(cde.isBaselineElement());

		cde = (CustomDataElementAssignment) testCDS
				.getCustomDataElementAssignment().get(1);
		assertFalse(cde.isBaselineElement());

		cde = (CustomDataElementAssignment) testCDS
				.getCustomDataElementAssignment().get(2);
		assertFalse(cde.isBaselineElement());

		// fourth element is a baseline element
		cde = (CustomDataElementAssignment) testCDS
				.getCustomDataElementAssignment().get(3);
		assertTrue(cde.isBaselineElement());

		// cleanup
		testCDS.setElementData4(null);
		testCDS.setDisplaySequenceNumber4(null);
		testCDS.setCompleteInd(false);
		testCDS = service.update(testCDS);
		assertNoProblems(testCDS);
		utils.flushCDSFormatArrayCache();
	}

	public void testCDSSortingFiltering() {
		doAsSubject(new PrivilegedAction() {
			public Object run() {
				executeTestCDSSortingFiltering();

				return null;
			}
		}, "MATTCACS");
	}

	/**
	 * Call test method to add a baseline element
	 */
	public void executeTestCDSSortingFiltering() {
		Account account = (Account) DomainObjectFactory.getInstance()
				.newObject(Account.CLASS_ID);
		account.setLocationCode("201112");
		account.setAccountNumber("0101");
		account = (Account) ServiceUtils.loadObjectByBK((ObjectBean) account,
				true);
		CustomDataSegment cds1 = (CustomDataSegment) DomainObjectFactory
				.getInstance().newObject(CustomDataSegment.CLASS_ID);
		CustomDataSegment cds2 = (CustomDataSegment) DomainObjectFactory
				.getInstance().newObject(CustomDataSegment.CLASS_ID);
		CustomDataSegment cds3 = (CustomDataSegment) DomainObjectFactory
				.getInstance().newObject(CustomDataSegment.CLASS_ID);
		CustomDataSegment cds4 = (CustomDataSegment) DomainObjectFactory
				.getInstance().newObject(CustomDataSegment.CLASS_ID);
		CustomDataSegment cds5 = (CustomDataSegment) DomainObjectFactory
				.getInstance().newObject(CustomDataSegment.CLASS_ID);
		CustomDataSegment cds6 = (CustomDataSegment) DomainObjectFactory
				.getInstance().newObject(CustomDataSegment.CLASS_ID);
		cds1.setSegmentID("BPO");
		cds1.setCustomDataSegmentData("");
		cds1.setSequenceNumber(9996);
		account.addChild(cds1);

		cds2.setSegmentID("BPO");
		cds2.setCustomDataSegmentData("");
		cds2.setSequenceNumber(9997);
		account.addChild(cds2);

		cds3.setSegmentID("BPO");
		cds3.setCustomDataSegmentData("");
		cds3.setSequenceNumber(9998);
		account.addChild(cds3);

		cds4.setSegmentID("ACL");
		cds4.setCustomDataSegmentData("");
		cds4.setSequenceNumber(9998);
		account.addChild(cds4);

		cds5.setSegmentID("ACL");
		cds5.setCustomDataSegmentData("");
		cds5.setSequenceNumber(9997);
		account.addChild(cds5);

		cds6.setSegmentID("ACL");
		cds6.setCustomDataSegmentData("");
		cds6.setSequenceNumber(9996);
		account.addChild(cds6);

		// test filtering first
		CustomDataSegment testCDS;
		List<CustomDataSegment> filteredCDS = account.getFilteredCDS(account
				.getCustomDataSegments(), "BPO");
		Iterator<CustomDataSegment> it = filteredCDS.iterator();
		while (it.hasNext()) {
			testCDS = (CustomDataSegment) it.next();
			assertEquals("BPO", testCDS.getSegmentID());
		}

		filteredCDS = account.getFilteredCDS(account.getCustomDataSegments(),
				"ACL");
		it = filteredCDS.iterator();
		while (it.hasNext()) {
			testCDS = (CustomDataSegment) it.next();
			assertEquals("ACL", testCDS.getSegmentID());
		}

		// test filtering and sorting to ensure we get current
		Integer seqNum = Integer.valueOf(9996);
		testCDS = account.getCurrentCustomDataSegment("BPO");
		assertEquals("BPO", testCDS.getSegmentID());
		assertTrue(seqNum.compareTo(testCDS.getSequenceNumber()) == 0);

		testCDS = account.getCurrentCustomDataSegment("ACL");
		assertEquals("ACL", testCDS.getSegmentID());
		assertTrue(seqNum.compareTo(testCDS.getSequenceNumber()) == 0);

		// test the sorted order
		testCDS = (CustomDataSegment) account.getSortedCustomDataSegment().get(
				0);
		seqNum = Integer.valueOf(9996);
		assertEquals("ACL", testCDS.getSegmentID());
		assertTrue(seqNum.compareTo(testCDS.getSequenceNumber()) == 0);

		testCDS = (CustomDataSegment) account.getSortedCustomDataSegment().get(
				1);
		seqNum = Integer.valueOf(9997);
		assertEquals("ACL", testCDS.getSegmentID());
		assertTrue(seqNum.compareTo(testCDS.getSequenceNumber()) == 0);

		testCDS = (CustomDataSegment) account.getSortedCustomDataSegment().get(
				2);
		seqNum = Integer.valueOf(9998);
		assertEquals("ACL", testCDS.getSegmentID());
		assertTrue(seqNum.compareTo(testCDS.getSequenceNumber()) == 0);

		testCDS = (CustomDataSegment) account.getSortedCustomDataSegment().get(
				3);
		seqNum = Integer.valueOf(9996);
		assertEquals("BPO", testCDS.getSegmentID());
		assertTrue(seqNum.compareTo(testCDS.getSequenceNumber()) == 0);

		testCDS = (CustomDataSegment) account.getSortedCustomDataSegment().get(
				4);
		seqNum = Integer.valueOf(9997);
		assertEquals("BPO", testCDS.getSegmentID());
		assertTrue(seqNum.compareTo(testCDS.getSequenceNumber()) == 0);

		testCDS = (CustomDataSegment) account.getSortedCustomDataSegment().get(
				5);
		seqNum = Integer.valueOf(9998);
		assertEquals("BPO", testCDS.getSegmentID());
		assertTrue(seqNum.compareTo(testCDS.getSequenceNumber()) == 0);
	}

	public void testPortfolioCDSSortingFiltering() {
		doAsSubject(new PrivilegedAction() {
			public Object run() {
				executeTestPortfolioCDSSortingFiltering();

				return null;
			}
		}, "MATTCACS");
	}

	/**
	 * Call test method to add a baseline element
	 */
	public void executeTestPortfolioCDSSortingFiltering() {
		CustomerPortfolio portfolio = (CustomerPortfolio) DomainObjectFactory
				.getInstance().newObject(CustomerPortfolio.CLASS_ID);
		portfolio.setLocationCode("401112");
		portfolio.setPortfolioId("CUST091000");
		portfolio = (CustomerPortfolio) ServiceUtils.loadObjectByBK(
				(ObjectBean) portfolio, true);
		PortfolioCustomDataSegment cds1 = (PortfolioCustomDataSegment) DomainObjectFactory
				.getInstance().newObject(PortfolioCustomDataSegment.CLASS_ID);
		PortfolioCustomDataSegment cds2 = (PortfolioCustomDataSegment) DomainObjectFactory
				.getInstance().newObject(PortfolioCustomDataSegment.CLASS_ID);
		PortfolioCustomDataSegment cds3 = (PortfolioCustomDataSegment) DomainObjectFactory
				.getInstance().newObject(PortfolioCustomDataSegment.CLASS_ID);
		PortfolioCustomDataSegment cds4 = (PortfolioCustomDataSegment) DomainObjectFactory
				.getInstance().newObject(PortfolioCustomDataSegment.CLASS_ID);
		PortfolioCustomDataSegment cds5 = (PortfolioCustomDataSegment) DomainObjectFactory
				.getInstance().newObject(PortfolioCustomDataSegment.CLASS_ID);
		PortfolioCustomDataSegment cds6 = (PortfolioCustomDataSegment) DomainObjectFactory
				.getInstance().newObject(PortfolioCustomDataSegment.CLASS_ID);
		cds1.setSegmentID("BPO");
		cds1.setCustomDataSegmentData("");
		cds1.setSequenceNumber(9996);
		portfolio.addChild(cds1);

		cds2.setSegmentID("BPO");
		cds2.setCustomDataSegmentData("");
		cds2.setSequenceNumber(9997);
		portfolio.addChild(cds2);

		cds3.setSegmentID("BPO");
		cds3.setCustomDataSegmentData("");
		cds3.setSequenceNumber(9998);
		portfolio.addChild(cds3);

		cds4.setSegmentID("ACL");
		cds4.setCustomDataSegmentData("");
		cds4.setSequenceNumber(9998);
		portfolio.addChild(cds4);

		cds5.setSegmentID("ACL");
		cds5.setCustomDataSegmentData("");
		cds5.setSequenceNumber(9997);
		portfolio.addChild(cds5);

		cds6.setSegmentID("ACL");
		cds6.setCustomDataSegmentData("");
		cds6.setSequenceNumber(9996);
		portfolio.addChild(cds6);

		// test filtering first
		PortfolioCustomDataSegment testCDS;
		List<PortfolioCustomDataSegment> filteredCDS = portfolio
				.getFilteredCDS(portfolio.getCustomDataSegments(), "BPO");
		Iterator<PortfolioCustomDataSegment> it = filteredCDS.iterator();
		while (it.hasNext()) {
			testCDS = (PortfolioCustomDataSegment) it.next();
			assertEquals("BPO", testCDS.getSegmentID());
		}

		filteredCDS = portfolio.getFilteredCDS(portfolio
				.getCustomDataSegments(), "ACL");
		it = filteredCDS.iterator();
		while (it.hasNext()) {
			testCDS = (PortfolioCustomDataSegment) it.next();
			assertEquals("ACL", testCDS.getSegmentID());
		}

		// test filtering and sorting to ensure we get current
		Integer seqNum = Integer.valueOf(9996);
		testCDS = portfolio.getCurrentCustomDataSegment("BPO");
		assertEquals("BPO", testCDS.getSegmentID());
		assertTrue(seqNum.compareTo(testCDS.getSequenceNumber()) == 0);

		testCDS = portfolio.getCurrentCustomDataSegment("ACL");
		assertEquals("ACL", testCDS.getSegmentID());
		assertTrue(seqNum.compareTo(testCDS.getSequenceNumber()) == 0);

		// test the sorted order
		testCDS = (PortfolioCustomDataSegment) portfolio.getSortedCustomDataSegment().get(0);
		seqNum = Integer.valueOf(9996);
		assertEquals("ACL", testCDS.getSegmentID());
		assertTrue(seqNum.compareTo(testCDS.getSequenceNumber()) == 0);

		testCDS = (PortfolioCustomDataSegment) portfolio
				.getSortedCustomDataSegment().get(
				1);
		seqNum = Integer.valueOf(9997);
		assertEquals("ACL", testCDS.getSegmentID());
		assertTrue(seqNum.compareTo(testCDS.getSequenceNumber()) == 0);

		testCDS = (PortfolioCustomDataSegment) portfolio
				.getSortedCustomDataSegment().get(
				2);
		seqNum = Integer.valueOf(9998);
		assertEquals("ACL", testCDS.getSegmentID());
		assertTrue(seqNum.compareTo(testCDS.getSequenceNumber()) == 0);

		testCDS = (PortfolioCustomDataSegment) portfolio
				.getSortedCustomDataSegment().get(
				3);
		seqNum = Integer.valueOf(9996);
		assertEquals("BPO", testCDS.getSegmentID());
		assertTrue(seqNum.compareTo(testCDS.getSequenceNumber()) == 0);

		testCDS = (PortfolioCustomDataSegment) portfolio
				.getSortedCustomDataSegment().get(
				4);
		seqNum = Integer.valueOf(9997);
		assertEquals("BPO", testCDS.getSegmentID());
		assertTrue(seqNum.compareTo(testCDS.getSequenceNumber()) == 0);

		testCDS = (PortfolioCustomDataSegment) portfolio.getSortedCustomDataSegment().get(
				5);
		seqNum = Integer.valueOf(9998);
		assertEquals("BPO", testCDS.getSegmentID());
		assertTrue(seqNum.compareTo(testCDS.getSequenceNumber()) == 0);
	}
}
