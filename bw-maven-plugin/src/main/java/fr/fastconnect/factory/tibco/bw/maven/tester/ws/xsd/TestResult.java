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
 * TestResult.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package fr.fastconnect.factory.tibco.bw.maven.tester.ws.xsd;

public class TestResult  extends TestType  implements java.io.Serializable {
	private static final long serialVersionUID = -6056103891176136832L;

	private TestFailure testFailure;

    private TestSuccess testSuccess;

    private java.util.Calendar datetime;  // attribute

    public TestResult() {
    }

    public TestResult(
           java.lang.String name,
           java.lang.String path,
           java.util.Calendar datetime,
           TestFailure testFailure,
           TestSuccess testSuccess) {
        super(
            name,
            path);
        this.datetime = datetime;
        this.testFailure = testFailure;
        this.testSuccess = testSuccess;
    }


    /**
     * Gets the testFailure value for this TestResult.
     * 
     * @return testFailure
     */
    public TestFailure getTestFailure() {
        return testFailure;
    }


    /**
     * Sets the testFailure value for this TestResult.
     * 
     * @param testFailure
     */
    public void setTestFailure(TestFailure testFailure) {
        this.testFailure = testFailure;
    }


    /**
     * Gets the testSuccess value for this TestResult.
     * 
     * @return testSuccess
     */
    public TestSuccess getTestSuccess() {
        return testSuccess;
    }


    /**
     * Sets the testSuccess value for this TestResult.
     * 
     * @param testSuccess
     */
    public void setTestSuccess(TestSuccess testSuccess) {
        this.testSuccess = testSuccess;
    }


    /**
     * Gets the datetime value for this TestResult.
     * 
     * @return datetime
     */
    public java.util.Calendar getDatetime() {
        return datetime;
    }


    /**
     * Sets the datetime value for this TestResult.
     * 
     * @param datetime
     */
    public void setDatetime(java.util.Calendar datetime) {
        this.datetime = datetime;
    }

    private java.lang.Object __equalsCalc = null;
    @SuppressWarnings("unused")
	public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof TestResult)) return false;
        TestResult other = (TestResult) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.testFailure==null && other.getTestFailure()==null) || 
             (this.testFailure!=null &&
              this.testFailure.equals(other.getTestFailure()))) &&
            ((this.testSuccess==null && other.getTestSuccess()==null) || 
             (this.testSuccess!=null &&
              this.testSuccess.equals(other.getTestSuccess()))) &&
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
        if (getTestFailure() != null) {
            _hashCode += getTestFailure().hashCode();
        }
        if (getTestSuccess() != null) {
            _hashCode += getTestSuccess().hashCode();
        }
        if (getDatetime() != null) {
            _hashCode += getDatetime().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(TestResult.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://fastconnect.fr/fcunit.xsd", ">test-result"));
        org.apache.axis.description.AttributeDesc attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("datetime");
        attrField.setXmlName(new javax.xml.namespace.QName("", "datetime"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        typeDesc.addFieldDesc(attrField);
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("testFailure");
        elemField.setXmlName(new javax.xml.namespace.QName("http://fastconnect.fr/fcunit.xsd", "test-failure"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://fastconnect.fr/fcunit.xsd", ">test-failure"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("testSuccess");
        elemField.setXmlName(new javax.xml.namespace.QName("http://fastconnect.fr/fcunit.xsd", "test-success"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://fastconnect.fr/fcunit.xsd", ">test-success"));
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
