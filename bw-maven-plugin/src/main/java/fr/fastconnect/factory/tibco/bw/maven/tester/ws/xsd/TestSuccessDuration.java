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
 * TestSuccessDuration.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package fr.fastconnect.factory.tibco.bw.maven.tester.ws.xsd;

public class TestSuccessDuration  implements java.io.Serializable {
	private static final long serialVersionUID = 5350614068250353293L;

	private org.apache.axis.types.UnsignedLong test;

    private org.apache.axis.types.UnsignedLong fixture;

    public TestSuccessDuration() {
    }

    public TestSuccessDuration(
           org.apache.axis.types.UnsignedLong test,
           org.apache.axis.types.UnsignedLong fixture) {
           this.test = test;
           this.fixture = fixture;
    }


    /**
     * Gets the test value for this TestSuccessDuration.
     * 
     * @return test
     */
    public org.apache.axis.types.UnsignedLong getTest() {
        return test;
    }


    /**
     * Sets the test value for this TestSuccessDuration.
     * 
     * @param test
     */
    public void setTest(org.apache.axis.types.UnsignedLong test) {
        this.test = test;
    }


    /**
     * Gets the fixture value for this TestSuccessDuration.
     * 
     * @return fixture
     */
    public org.apache.axis.types.UnsignedLong getFixture() {
        return fixture;
    }


    /**
     * Sets the fixture value for this TestSuccessDuration.
     * 
     * @param fixture
     */
    public void setFixture(org.apache.axis.types.UnsignedLong fixture) {
        this.fixture = fixture;
    }

    private java.lang.Object __equalsCalc = null;
    @SuppressWarnings("unused")
	public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof TestSuccessDuration)) return false;
        TestSuccessDuration other = (TestSuccessDuration) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.test==null && other.getTest()==null) || 
             (this.test!=null &&
              this.test.equals(other.getTest()))) &&
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
        int _hashCode = 1;
        if (getTest() != null) {
            _hashCode += getTest().hashCode();
        }
        if (getFixture() != null) {
            _hashCode += getFixture().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(TestSuccessDuration.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://fastconnect.fr/fcunit.xsd", ">>test-success>duration"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("test");
        elemField.setXmlName(new javax.xml.namespace.QName("http://fastconnect.fr/fcunit.xsd", "test"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "unsignedLong"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fixture");
        elemField.setXmlName(new javax.xml.namespace.QName("http://fastconnect.fr/fcunit.xsd", "fixture"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "unsignedLong"));
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
