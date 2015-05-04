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
 * LicenseContent.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package fr.fastconnect.factory.tibco.bw.maven.tester.ws.xsd;

public class LicenseContent  implements java.io.Serializable {
	private static final long serialVersionUID = 1714116087687110788L;

	private java.lang.String holder;

    private java.util.Calendar issued;

    private java.lang.String issuer;

    private java.util.Calendar notAfter;

    private java.util.Calendar notBefore;

    private java.lang.String subject;

    public LicenseContent() {
    }

    public LicenseContent(
           java.lang.String holder,
           java.util.Calendar issued,
           java.lang.String issuer,
           java.util.Calendar notAfter,
           java.util.Calendar notBefore,
           java.lang.String subject) {
           this.holder = holder;
           this.issued = issued;
           this.issuer = issuer;
           this.notAfter = notAfter;
           this.notBefore = notBefore;
           this.subject = subject;
    }


    /**
     * Gets the holder value for this LicenseContent.
     * 
     * @return holder
     */
    public java.lang.String getHolder() {
        return holder;
    }


    /**
     * Sets the holder value for this LicenseContent.
     * 
     * @param holder
     */
    public void setHolder(java.lang.String holder) {
        this.holder = holder;
    }


    /**
     * Gets the issued value for this LicenseContent.
     * 
     * @return issued
     */
    public java.util.Calendar getIssued() {
        return issued;
    }


    /**
     * Sets the issued value for this LicenseContent.
     * 
     * @param issued
     */
    public void setIssued(java.util.Calendar issued) {
        this.issued = issued;
    }


    /**
     * Gets the issuer value for this LicenseContent.
     * 
     * @return issuer
     */
    public java.lang.String getIssuer() {
        return issuer;
    }


    /**
     * Sets the issuer value for this LicenseContent.
     * 
     * @param issuer
     */
    public void setIssuer(java.lang.String issuer) {
        this.issuer = issuer;
    }


    /**
     * Gets the notAfter value for this LicenseContent.
     * 
     * @return notAfter
     */
    public java.util.Calendar getNotAfter() {
        return notAfter;
    }


    /**
     * Sets the notAfter value for this LicenseContent.
     * 
     * @param notAfter
     */
    public void setNotAfter(java.util.Calendar notAfter) {
        this.notAfter = notAfter;
    }


    /**
     * Gets the notBefore value for this LicenseContent.
     * 
     * @return notBefore
     */
    public java.util.Calendar getNotBefore() {
        return notBefore;
    }


    /**
     * Sets the notBefore value for this LicenseContent.
     * 
     * @param notBefore
     */
    public void setNotBefore(java.util.Calendar notBefore) {
        this.notBefore = notBefore;
    }


    /**
     * Gets the subject value for this LicenseContent.
     * 
     * @return subject
     */
    public java.lang.String getSubject() {
        return subject;
    }


    /**
     * Sets the subject value for this LicenseContent.
     * 
     * @param subject
     */
    public void setSubject(java.lang.String subject) {
        this.subject = subject;
    }

    private java.lang.Object __equalsCalc = null;
    @SuppressWarnings("unused")
	public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof LicenseContent)) return false;
        LicenseContent other = (LicenseContent) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.holder==null && other.getHolder()==null) || 
             (this.holder!=null &&
              this.holder.equals(other.getHolder()))) &&
            ((this.issued==null && other.getIssued()==null) || 
             (this.issued!=null &&
              this.issued.equals(other.getIssued()))) &&
            ((this.issuer==null && other.getIssuer()==null) || 
             (this.issuer!=null &&
              this.issuer.equals(other.getIssuer()))) &&
            ((this.notAfter==null && other.getNotAfter()==null) || 
             (this.notAfter!=null &&
              this.notAfter.equals(other.getNotAfter()))) &&
            ((this.notBefore==null && other.getNotBefore()==null) || 
             (this.notBefore!=null &&
              this.notBefore.equals(other.getNotBefore()))) &&
            ((this.subject==null && other.getSubject()==null) || 
             (this.subject!=null &&
              this.subject.equals(other.getSubject())));
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
        if (getHolder() != null) {
            _hashCode += getHolder().hashCode();
        }
        if (getIssued() != null) {
            _hashCode += getIssued().hashCode();
        }
        if (getIssuer() != null) {
            _hashCode += getIssuer().hashCode();
        }
        if (getNotAfter() != null) {
            _hashCode += getNotAfter().hashCode();
        }
        if (getNotBefore() != null) {
            _hashCode += getNotBefore().hashCode();
        }
        if (getSubject() != null) {
            _hashCode += getSubject().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(LicenseContent.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://fastconnect.fr/fcunit.xsd", ">license-content"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("holder");
        elemField.setXmlName(new javax.xml.namespace.QName("http://fastconnect.fr/fcunit.xsd", "holder"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("issued");
        elemField.setXmlName(new javax.xml.namespace.QName("http://fastconnect.fr/fcunit.xsd", "issued"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("issuer");
        elemField.setXmlName(new javax.xml.namespace.QName("http://fastconnect.fr/fcunit.xsd", "issuer"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("notAfter");
        elemField.setXmlName(new javax.xml.namespace.QName("http://fastconnect.fr/fcunit.xsd", "not-after"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("notBefore");
        elemField.setXmlName(new javax.xml.namespace.QName("http://fastconnect.fr/fcunit.xsd", "not-before"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("subject");
        elemField.setXmlName(new javax.xml.namespace.QName("http://fastconnect.fr/fcunit.xsd", "subject"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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
