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
 * TestFailureErrorReport.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package fr.fastconnect.factory.tibco.bw.maven.tester.ws.xsd;

public class TestFailureErrorReport  implements java.io.Serializable {
	private static final long serialVersionUID = -2997595110893501783L;

	private java.lang.String stackTrace;

    private java.lang.String msg;

    private java.lang.String fullClass;

    private java.lang.String _class;

    private java.lang.String[] processStack;

    private java.lang.String msgCode;

    private Anydata data;

    public TestFailureErrorReport() {
    }

    public TestFailureErrorReport(
           java.lang.String stackTrace,
           java.lang.String msg,
           java.lang.String fullClass,
           java.lang.String _class,
           java.lang.String[] processStack,
           java.lang.String msgCode,
           Anydata data) {
           this.stackTrace = stackTrace;
           this.msg = msg;
           this.fullClass = fullClass;
           this._class = _class;
           this.processStack = processStack;
           this.msgCode = msgCode;
           this.data = data;
    }


    /**
     * Gets the stackTrace value for this TestFailureErrorReport.
     * 
     * @return stackTrace
     */
    public java.lang.String getStackTrace() {
        return stackTrace;
    }


    /**
     * Sets the stackTrace value for this TestFailureErrorReport.
     * 
     * @param stackTrace
     */
    public void setStackTrace(java.lang.String stackTrace) {
        this.stackTrace = stackTrace;
    }


    /**
     * Gets the msg value for this TestFailureErrorReport.
     * 
     * @return msg
     */
    public java.lang.String getMsg() {
        return msg;
    }


    /**
     * Sets the msg value for this TestFailureErrorReport.
     * 
     * @param msg
     */
    public void setMsg(java.lang.String msg) {
        this.msg = msg;
    }


    /**
     * Gets the fullClass value for this TestFailureErrorReport.
     * 
     * @return fullClass
     */
    public java.lang.String getFullClass() {
        return fullClass;
    }


    /**
     * Sets the fullClass value for this TestFailureErrorReport.
     * 
     * @param fullClass
     */
    public void setFullClass(java.lang.String fullClass) {
        this.fullClass = fullClass;
    }


    /**
     * Gets the _class value for this TestFailureErrorReport.
     * 
     * @return _class
     */
    public java.lang.String get_class() {
        return _class;
    }


    /**
     * Sets the _class value for this TestFailureErrorReport.
     * 
     * @param _class
     */
    public void set_class(java.lang.String _class) {
        this._class = _class;
    }


    /**
     * Gets the processStack value for this TestFailureErrorReport.
     * 
     * @return processStack
     */
    public java.lang.String[] getProcessStack() {
        return processStack;
    }


    /**
     * Sets the processStack value for this TestFailureErrorReport.
     * 
     * @param processStack
     */
    public void setProcessStack(java.lang.String[] processStack) {
        this.processStack = processStack;
    }


    /**
     * Gets the msgCode value for this TestFailureErrorReport.
     * 
     * @return msgCode
     */
    public java.lang.String getMsgCode() {
        return msgCode;
    }


    /**
     * Sets the msgCode value for this TestFailureErrorReport.
     * 
     * @param msgCode
     */
    public void setMsgCode(java.lang.String msgCode) {
        this.msgCode = msgCode;
    }


    /**
     * Gets the data value for this TestFailureErrorReport.
     * 
     * @return data
     */
    public Anydata getData() {
        return data;
    }


    /**
     * Sets the data value for this TestFailureErrorReport.
     * 
     * @param data
     */
    public void setData(Anydata data) {
        this.data = data;
    }

    private java.lang.Object __equalsCalc = null;
    @SuppressWarnings("unused")
	public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof TestFailureErrorReport)) return false;
        TestFailureErrorReport other = (TestFailureErrorReport) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.stackTrace==null && other.getStackTrace()==null) || 
             (this.stackTrace!=null &&
              this.stackTrace.equals(other.getStackTrace()))) &&
            ((this.msg==null && other.getMsg()==null) || 
             (this.msg!=null &&
              this.msg.equals(other.getMsg()))) &&
            ((this.fullClass==null && other.getFullClass()==null) || 
             (this.fullClass!=null &&
              this.fullClass.equals(other.getFullClass()))) &&
            ((this._class==null && other.get_class()==null) || 
             (this._class!=null &&
              this._class.equals(other.get_class()))) &&
            ((this.processStack==null && other.getProcessStack()==null) || 
             (this.processStack!=null &&
              java.util.Arrays.equals(this.processStack, other.getProcessStack()))) &&
            ((this.msgCode==null && other.getMsgCode()==null) || 
             (this.msgCode!=null &&
              this.msgCode.equals(other.getMsgCode()))) &&
            ((this.data==null && other.getData()==null) || 
             (this.data!=null &&
              this.data.equals(other.getData())));
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
        if (getStackTrace() != null) {
            _hashCode += getStackTrace().hashCode();
        }
        if (getMsg() != null) {
            _hashCode += getMsg().hashCode();
        }
        if (getFullClass() != null) {
            _hashCode += getFullClass().hashCode();
        }
        if (get_class() != null) {
            _hashCode += get_class().hashCode();
        }
        if (getProcessStack() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getProcessStack());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getProcessStack(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getMsgCode() != null) {
            _hashCode += getMsgCode().hashCode();
        }
        if (getData() != null) {
            _hashCode += getData().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(TestFailureErrorReport.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://fastconnect.fr/fcunit.xsd", ">>test-failure>error-report"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("stackTrace");
        elemField.setXmlName(new javax.xml.namespace.QName("http://fastconnect.fr/fcunit.xsd", "StackTrace"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("msg");
        elemField.setXmlName(new javax.xml.namespace.QName("http://fastconnect.fr/fcunit.xsd", "Msg"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fullClass");
        elemField.setXmlName(new javax.xml.namespace.QName("http://fastconnect.fr/fcunit.xsd", "FullClass"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("_class");
        elemField.setXmlName(new javax.xml.namespace.QName("http://fastconnect.fr/fcunit.xsd", "Class"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("processStack");
        elemField.setXmlName(new javax.xml.namespace.QName("http://fastconnect.fr/fcunit.xsd", "ProcessStack"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://fastconnect.fr/fcunit.xsd", "ProcessName"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("msgCode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://fastconnect.fr/fcunit.xsd", "MsgCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("data");
        elemField.setXmlName(new javax.xml.namespace.QName("http://fastconnect.fr/fcunit.xsd", "Data"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://fastconnect.fr/fcunit.xsd", "anydata"));
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
