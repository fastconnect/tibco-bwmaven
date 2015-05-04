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
 * Settings.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package fr.fastconnect.factory.tibco.bw.maven.tester.ws.xsd;

public class Settings  implements java.io.Serializable {
	private static final long serialVersionUID = -7857949183853115788L;

	private java.lang.String scope;

    private java.lang.String suitePattern;

    private java.lang.String casePattern;

    private java.lang.String fixturePattern;

    private boolean exportToFiles;

    private java.lang.String exportDirectory;

    public Settings() {
    }

    public Settings(
           java.lang.String scope,
           java.lang.String suitePattern,
           java.lang.String casePattern,
           java.lang.String fixturePattern,
           boolean exportToFiles,
           java.lang.String exportDirectory) {
           this.scope = scope;
           this.suitePattern = suitePattern;
           this.casePattern = casePattern;
           this.fixturePattern = fixturePattern;
           this.exportToFiles = exportToFiles;
           this.exportDirectory = exportDirectory;
    }


    /**
     * Gets the scope value for this Settings.
     * 
     * @return scope
     */
    public java.lang.String getScope() {
        return scope;
    }


    /**
     * Sets the scope value for this Settings.
     * 
     * @param scope
     */
    public void setScope(java.lang.String scope) {
        this.scope = scope;
    }


    /**
     * Gets the suitePattern value for this Settings.
     * 
     * @return suitePattern
     */
    public java.lang.String getSuitePattern() {
        return suitePattern;
    }


    /**
     * Sets the suitePattern value for this Settings.
     * 
     * @param suitePattern
     */
    public void setSuitePattern(java.lang.String suitePattern) {
        this.suitePattern = suitePattern;
    }


    /**
     * Gets the casePattern value for this Settings.
     * 
     * @return casePattern
     */
    public java.lang.String getCasePattern() {
        return casePattern;
    }


    /**
     * Sets the casePattern value for this Settings.
     * 
     * @param casePattern
     */
    public void setCasePattern(java.lang.String casePattern) {
        this.casePattern = casePattern;
    }


    /**
     * Gets the fixturePattern value for this Settings.
     * 
     * @return fixturePattern
     */
    public java.lang.String getFixturePattern() {
        return fixturePattern;
    }


    /**
     * Sets the fixturePattern value for this Settings.
     * 
     * @param fixturePattern
     */
    public void setFixturePattern(java.lang.String fixturePattern) {
        this.fixturePattern = fixturePattern;
    }


    /**
     * Gets the exportToFiles value for this Settings.
     * 
     * @return exportToFiles
     */
    public boolean isExportToFiles() {
        return exportToFiles;
    }


    /**
     * Sets the exportToFiles value for this Settings.
     * 
     * @param exportToFiles
     */
    public void setExportToFiles(boolean exportToFiles) {
        this.exportToFiles = exportToFiles;
    }


    /**
     * Gets the exportDirectory value for this Settings.
     * 
     * @return exportDirectory
     */
    public java.lang.String getExportDirectory() {
        return exportDirectory;
    }


    /**
     * Sets the exportDirectory value for this Settings.
     * 
     * @param exportDirectory
     */
    public void setExportDirectory(java.lang.String exportDirectory) {
        this.exportDirectory = exportDirectory;
    }

    private java.lang.Object __equalsCalc = null;
    @SuppressWarnings("unused")
	public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Settings)) return false;
        Settings other = (Settings) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.scope==null && other.getScope()==null) || 
             (this.scope!=null &&
              this.scope.equals(other.getScope()))) &&
            ((this.suitePattern==null && other.getSuitePattern()==null) || 
             (this.suitePattern!=null &&
              this.suitePattern.equals(other.getSuitePattern()))) &&
            ((this.casePattern==null && other.getCasePattern()==null) || 
             (this.casePattern!=null &&
              this.casePattern.equals(other.getCasePattern()))) &&
            ((this.fixturePattern==null && other.getFixturePattern()==null) || 
             (this.fixturePattern!=null &&
              this.fixturePattern.equals(other.getFixturePattern()))) &&
            this.exportToFiles == other.isExportToFiles() &&
            ((this.exportDirectory==null && other.getExportDirectory()==null) || 
             (this.exportDirectory!=null &&
              this.exportDirectory.equals(other.getExportDirectory())));
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
        if (getScope() != null) {
            _hashCode += getScope().hashCode();
        }
        if (getSuitePattern() != null) {
            _hashCode += getSuitePattern().hashCode();
        }
        if (getCasePattern() != null) {
            _hashCode += getCasePattern().hashCode();
        }
        if (getFixturePattern() != null) {
            _hashCode += getFixturePattern().hashCode();
        }
        _hashCode += (isExportToFiles() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        if (getExportDirectory() != null) {
            _hashCode += getExportDirectory().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Settings.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://fastconnect.fr/fcunit.xsd", ">settings"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("scope");
        elemField.setXmlName(new javax.xml.namespace.QName("http://fastconnect.fr/fcunit.xsd", "scope"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("suitePattern");
        elemField.setXmlName(new javax.xml.namespace.QName("http://fastconnect.fr/fcunit.xsd", "suite-pattern"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("casePattern");
        elemField.setXmlName(new javax.xml.namespace.QName("http://fastconnect.fr/fcunit.xsd", "case-pattern"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fixturePattern");
        elemField.setXmlName(new javax.xml.namespace.QName("http://fastconnect.fr/fcunit.xsd", "fixture-pattern"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("exportToFiles");
        elemField.setXmlName(new javax.xml.namespace.QName("http://fastconnect.fr/fcunit.xsd", "export-to-files"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("exportDirectory");
        elemField.setXmlName(new javax.xml.namespace.QName("http://fastconnect.fr/fcunit.xsd", "export-directory"));
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
