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
 * TestCaseResult.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package fr.fastconnect.factory.tibco.bw.maven.tester.ws.xsd;

public class TestCaseResult  extends TestType  implements java.io.Serializable {
	private static final long serialVersionUID = -3220332243910757655L;

	private TestResult[] testResult;

    private java.util.Calendar datetime;  // attribute

    public TestCaseResult() {
    }

    public TestCaseResult(
           java.lang.String name,
           java.lang.String path,
           java.util.Calendar datetime,
           TestResult[] testResult) {
        super(
            name,
            path);
        this.datetime = datetime;
        this.testResult = testResult;
    }


    /**
     * Gets the testResult value for this TestCaseResult.
     * 
     * @return testResult
     */
    public TestResult[] getTestResult() {
        return testResult;
    }


    /**
     * Sets the testResult value for this TestCaseResult.
     * 
     * @param testResult
     */
    public void setTestResult(TestResult[] testResult) {
        this.testResult = testResult;
    }

    public TestResult getTestResult(int i) {
        return this.testResult[i];
    }

    public void setTestResult(int i, TestResult _value) {
        this.testResult[i] = _value;
    }


    /**
     * Gets the datetime value for this TestCaseResult.
     * 
     * @return datetime
     */
    public java.util.Calendar getDatetime() {
        return datetime;
    }


    /**
     * Sets the datetime value for this TestCaseResult.
     * 
     * @param datetime
     */
    public void setDatetime(java.util.Calendar datetime) {
        this.datetime = datetime;
    }

    private java.lang.Object __equalsCalc = null;
    @SuppressWarnings("unused")
	public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof TestCaseResult)) return false;
        TestCaseResult other = (TestCaseResult) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.testResult==null && other.getTestResult()==null) || 
             (this.testResult!=null &&
              java.util.Arrays.equals(this.testResult, other.getTestResult()))) &&
            ((this.datetime==null && other.getDatetime()==null) || 
             (this.datetime!=null &&
              this.datetime.equals(other.getDatetime())));
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
        if (getTestResult() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getTestResult());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getTestResult(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getDatetime() != null) {
            _hashCode += getDatetime().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(TestCaseResult.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://fastconnect.fr/fcunit.xsd", ">test-case-result"));
        org.apache.axis.description.AttributeDesc attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("datetime");
        attrField.setXmlName(new javax.xml.namespace.QName("", "datetime"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        typeDesc.addFieldDesc(attrField);
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("testResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://fastconnect.fr/fcunit.xsd", "test-result"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://fastconnect.fr/fcunit.xsd", "test-result"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
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
