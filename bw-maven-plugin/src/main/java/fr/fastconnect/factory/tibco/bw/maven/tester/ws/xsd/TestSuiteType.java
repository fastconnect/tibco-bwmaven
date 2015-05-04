/**
 * (C) Copyright 2011-2015 FastConnect SAS
 * (http://www.fastconnect.fr/) and others.
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
/**
 * TestSuiteType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package fr.fastconnect.factory.tibco.bw.maven.tester.ws.xsd;

public class TestSuiteType  extends TestType  implements java.io.Serializable {
	private static final long serialVersionUID = 8040700782179199362L;

	private TestSuiteType[] testSuite;

    private TestCaseType[] testCase;

    private Fixture fixture;

    public TestSuiteType() {
    }

    public TestSuiteType(
           java.lang.String name,
           java.lang.String path,
           TestSuiteType[] testSuite,
           TestCaseType[] testCase,
           Fixture fixture) {
        super(
            name,
            path);
        this.testSuite = testSuite;
        this.testCase = testCase;
        this.fixture = fixture;
    }


    /**
     * Gets the testSuite value for this TestSuiteType.
     * 
     * @return testSuite
     */
    public TestSuiteType[] getTestSuite() {
        return testSuite;
    }


    /**
     * Sets the testSuite value for this TestSuiteType.
     * 
     * @param testSuite
     */
    public void setTestSuite(TestSuiteType[] testSuite) {
        this.testSuite = testSuite;
    }

    public TestSuiteType getTestSuite(int i) {
        return this.testSuite[i];
    }

    public void setTestSuite(int i, TestSuiteType _value) {
        this.testSuite[i] = _value;
    }


    /**
     * Gets the testCase value for this TestSuiteType.
     * 
     * @return testCase
     */
    public TestCaseType[] getTestCase() {
        return testCase;
    }


    /**
     * Sets the testCase value for this TestSuiteType.
     * 
     * @param testCase
     */
    public void setTestCase(TestCaseType[] testCase) {
        this.testCase = testCase;
    }

    public TestCaseType getTestCase(int i) {
        return this.testCase[i];
    }

    public void setTestCase(int i, TestCaseType _value) {
        this.testCase[i] = _value;
    }


    /**
     * Gets the fixture value for this TestSuiteType.
     * 
     * @return fixture
     */
    public Fixture getFixture() {
        return fixture;
    }


    /**
     * Sets the fixture value for this TestSuiteType.
     * 
     * @param fixture
     */
    public void setFixture(Fixture fixture) {
        this.fixture = fixture;
    }

    private java.lang.Object __equalsCalc = null;
    @SuppressWarnings("unused")
	public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof TestSuiteType)) return false;
        TestSuiteType other = (TestSuiteType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.testSuite==null && other.getTestSuite()==null) || 
             (this.testSuite!=null &&
              java.util.Arrays.equals(this.testSuite, other.getTestSuite()))) &&
            ((this.testCase==null && other.getTestCase()==null) || 
             (this.testCase!=null &&
              java.util.Arrays.equals(this.testCase, other.getTestCase()))) &&
            ((this.fixture==null && other.getFixture()==null) || 
             (this.fixture!=null &&
              this.fixture.equals(other.getFixture())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = super.hashCode();
        if (getTestSuite() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getTestSuite());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getTestSuite(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getTestCase() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getTestCase());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getTestCase(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getFixture() != null) {
            _hashCode += getFixture().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(TestSuiteType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://fastconnect.fr/fcunit.xsd", "test-suite-type"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("testSuite");
        elemField.setXmlName(new javax.xml.namespace.QName("http://fastconnect.fr/fcunit.xsd", "test-suite"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://fastconnect.fr/fcunit.xsd", "test-suite"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("testCase");
        elemField.setXmlName(new javax.xml.namespace.QName("http://fastconnect.fr/fcunit.xsd", "test-case"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://fastconnect.fr/fcunit.xsd", "test-case"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fixture");
        elemField.setXmlName(new javax.xml.namespace.QName("http://fastconnect.fr/fcunit.xsd", "fixture"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://fastconnect.fr/fcunit.xsd", ">fixture"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    @SuppressWarnings("rawtypes")
	public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    @SuppressWarnings("rawtypes")
	public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
