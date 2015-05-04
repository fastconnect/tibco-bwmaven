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
 * TestFailure.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package fr.fastconnect.factory.tibco.bw.maven.tester.ws.xsd;

public class TestFailure  implements java.io.Serializable {
	private static final long serialVersionUID = 7629280876837906399L;
	
	private TestFailureErrorReport errorReport;

    public TestFailure() {
    }

    public TestFailure(
           TestFailureErrorReport errorReport) {
           this.errorReport = errorReport;
    }


    /**
     * Gets the errorReport value for this TestFailure.
     * 
     * @return errorReport
     */
    public TestFailureErrorReport getErrorReport() {
        return errorReport;
    }


    /**
     * Sets the errorReport value for this TestFailure.
     * 
     * @param errorReport
     */
    public void setErrorReport(TestFailureErrorReport errorReport) {
        this.errorReport = errorReport;
    }

    private java.lang.Object __equalsCalc = null;
    @SuppressWarnings("unused")
	public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof TestFailure)) return false;
        TestFailure other = (TestFailure) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.errorReport==null && other.getErrorReport()==null) || 
             (this.errorReport!=null &&
              this.errorReport.equals(other.getErrorReport())));
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
        if (getErrorReport() != null) {
            _hashCode += getErrorReport().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(TestFailure.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://fastconnect.fr/fcunit.xsd", ">test-failure"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("errorReport");
        elemField.setXmlName(new javax.xml.namespace.QName("http://fastconnect.fr/fcunit.xsd", "error-report"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://fastconnect.fr/fcunit.xsd", ">>test-failure>error-report"));
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
