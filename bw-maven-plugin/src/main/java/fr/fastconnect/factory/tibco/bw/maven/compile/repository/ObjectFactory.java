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
//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.7 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2014.09.10 à 11:00:34 AM CEST 
//


package fr.fastconnect.factory.tibco.bw.maven.compile.repository;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the fr.fastconnect.factory.tibco.bw.maven.compile.repository package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: fr.fastconnect.factory.tibco.bw.maven.compile.repository
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GlobalVariables }
     * 
     */
    public GlobalVariables createGlobalVariables() {
        return new GlobalVariables();
    }

    /**
     * Create an instance of {@link Repository }
     * 
     */
    public Repository createRepository() {
        return new Repository();
    }

    /**
     * Create an instance of {@link Designer }
     * 
     */
    public Designer createDesigner() {
        return new Designer();
    }

    /**
     * Create an instance of {@link AnyObject }
     * 
     */
    public AnyObject createAnyObject() {
        return new AnyObject();
    }

    /**
     * Create an instance of {@link Object }
     * 
     */
    public Object createObject() {
        return new Object();
    }

    /**
     * Create an instance of {@link GlobalVariables.GlobalVariable }
     * 
     */
    public GlobalVariables.GlobalVariable createGlobalVariablesGlobalVariable() {
        return new GlobalVariables.GlobalVariable();
    }

    /**
     * Create an instance of {@link GlobalNameRefType }
     * 
     */
    public GlobalNameRefType createGlobalNameRefType() {
        return new GlobalNameRefType();
    }

    /**
     * Create an instance of {@link SchemaGlobalNameRefType }
     * 
     */
    public SchemaGlobalNameRefType createSchemaGlobalNameRefType() {
        return new SchemaGlobalNameRefType();
    }

    /**
     * Create an instance of {@link Bytes }
     * 
     */
    public Bytes createBytes() {
        return new Bytes();
    }

    /**
     * Create an instance of {@link RefType }
     * 
     */
    public RefType createRefType() {
        return new RefType();
    }

}
