/*
* CDDL HEADER START
*
* The contents of this file are subject to the terms of the
* Common Development and Distribution License, Version 1.0 only
* (the "License"). You may not use this file except in compliance
* with the License.
*
* You can obtain a copy of the license at license/ESCIDOC.LICENSE
* or http://www.escidoc.de/license.
* See the License for the specific language governing permissions
* and limitations under the License.
*
* When distributing Covered Code, include this CDDL HEADER in each
* file and include the License file at license/ESCIDOC.LICENSE.
* If applicable, add the following below this CDDL HEADER, with the
* fields enclosed by brackets "[]" replaced with your own identifying
* information: Portions Copyright [yyyy] [name of copyright owner]
*
* CDDL HEADER END
*/

/*
* Copyright 2006-2007 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/

/**
 * ValidationSoapBindingStub.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */
package de.mpg.escidoc.services.validation.webservice;

import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.Vector;
import javax.xml.namespace.QName;
import org.apache.axis.AxisFault;
import org.apache.axis.NoEndPointException;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.client.Stub;
import org.apache.axis.constants.Style;
import org.apache.axis.constants.Use;
import org.apache.axis.description.OperationDesc;
import org.apache.axis.description.ParameterDesc;
import org.apache.axis.encoding.XMLType;
import org.apache.axis.soap.SOAPConstants;
import org.apache.axis.utils.JavaUtils;

public class ValidationSoapBindingStub extends Stub implements
        de.mpg.escidoc.services.validation.webservice.ItemValidatingWebService
{
    private Vector cachedSerClasses = new Vector();
    private Vector cachedSerQNames = new Vector();
    private Vector cachedSerFactories = new Vector();
    private Vector cachedDeserFactories = new Vector();
    static OperationDesc[] _operations;
    static
    {
        _operations = new OperationDesc[4];
        _initOperationDesc1();
    }

    private static void _initOperationDesc1()
    {
        OperationDesc oper;
        ParameterDesc param;
        oper = new OperationDesc();
        oper.setName("validateItemXml");
        param = new ParameterDesc(new QName("", "in0"), ParameterDesc.IN, new QName("http://www.w3.org/2001/XMLSchema",
                "string"), String.class, false, false);
        oper.addParameter(param);
        param = new ParameterDesc(new QName("", "in1"), ParameterDesc.IN, new QName("http://www.w3.org/2001/XMLSchema",
                "string"), String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new QName("http://www.w3.org/2001/XMLSchema", "string"));
        oper.setReturnClass(String.class);
        oper.setReturnQName(new QName("", "validateItemXmlReturn"));
        oper.setStyle(Style.RPC);
        oper.setUse(Use.ENCODED);
        _operations[0] = oper;
        
        oper = new OperationDesc();
        oper.setName("validateItemXml");
        param = new ParameterDesc(new QName("", "in0"), ParameterDesc.IN, new QName("http://www.w3.org/2001/XMLSchema",
                "string"), String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new QName("http://www.w3.org/2001/XMLSchema", "string"));
        oper.setReturnClass(String.class);
        oper.setReturnQName(new QName("", "validateItemXmlReturn"));
        oper.setStyle(Style.RPC);
        oper.setUse(Use.ENCODED);
        _operations[1] = oper;
        
        oper = new OperationDesc();
        oper.setName("validateItemXmlBySchema");
        param = new ParameterDesc(new QName("", "in0"), ParameterDesc.IN, new QName("http://www.w3.org/2001/XMLSchema",
                "string"), String.class, false, false);
        oper.addParameter(param);
        param = new ParameterDesc(new QName("", "in1"), ParameterDesc.IN, new QName("http://www.w3.org/2001/XMLSchema",
                "string"), String.class, false, false);
        oper.addParameter(param);
        param = new ParameterDesc(new QName("", "in2"), ParameterDesc.IN, new QName("http://www.w3.org/2001/XMLSchema",
                "string"), String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new QName("http://www.w3.org/2001/XMLSchema", "string"));
        oper.setReturnClass(String.class);
        oper.setReturnQName(new QName("", "validateItemXmlBySchemaReturn"));
        oper.setStyle(Style.RPC);
        oper.setUse(Use.ENCODED);
        _operations[2] = oper;
        
        oper = new OperationDesc();
        oper.setName("refreshValidationSchemaCache");
        oper.setReturnType(XMLType.AXIS_VOID);
        oper.setStyle(Style.RPC);
        oper.setUse(Use.ENCODED);
        _operations[3] = oper;
    }

    public ValidationSoapBindingStub() throws AxisFault
    {
        this(null);
    }

    public ValidationSoapBindingStub(java.net.URL endpointURL, Service service) throws AxisFault
    {
        this(service);
        super.cachedEndpoint = endpointURL;
    }

    public ValidationSoapBindingStub(Service service) throws AxisFault
    {
        if (service == null)
        {
            super.service = new Service();
        }
        else
        {
            super.service = service;
        }
        ((Service)super.service).setTypeMappingVersion("1.2");
    }

    protected Call createCall() throws java.rmi.RemoteException
    {
        try
        {
            Call _call = super._createCall();
            if (super.maintainSessionSet)
            {
                _call.setMaintainSession(super.maintainSession);
            }
            if (super.cachedUsername != null)
            {
                _call.setUsername(super.cachedUsername);
            }
            if (super.cachedPassword != null)
            {
                _call.setPassword(super.cachedPassword);
            }
            if (super.cachedEndpoint != null)
            {
                _call.setTargetEndpointAddress(super.cachedEndpoint);
            }
            if (super.cachedTimeout != null)
            {
                _call.setTimeout(super.cachedTimeout);
            }
            if (super.cachedPortName != null)
            {
                _call.setPortName(super.cachedPortName);
            }
            Enumeration keys = super.cachedProperties.keys();
            while (keys.hasMoreElements())
            {
                String key = (String)keys.nextElement();
                _call.setProperty(key, super.cachedProperties.get(key));
            }
            return _call;
        }
        catch (Throwable _t)
        {
            throw new AxisFault("Failure trying to get the Call object", _t);
        }
    }

    public String validateItemXml(String in0, String in1) throws java.rmi.RemoteException
    {
        if (super.cachedEndpoint == null)
        {
            throw new NoEndPointException();
        }
        Call _call = createCall();
        _call.setOperation(_operations[0]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new QName("http://www.escidoc.de/", "validateItemXml"));
        setRequestHeaders(_call);
        setAttachments(_call);
        try
        {
            Object _resp = _call.invoke(new Object[] { in0, in1 });
            if (_resp instanceof java.rmi.RemoteException)
            {
                throw (java.rmi.RemoteException)_resp;
            }
            else
            {
                extractAttachments(_call);
                try
                {
                    return (String)_resp;
                }
                catch (Exception _exception)
                {
                    return (String)JavaUtils.convert(_resp, String.class);
                }
            }
        }
        catch (AxisFault axisFaultException)
        {
            throw axisFaultException;
        }
    }

    public String validateItemXml(String in0) throws java.rmi.RemoteException
    {
        if (super.cachedEndpoint == null)
        {
            throw new NoEndPointException();
        }
        Call _call = createCall();
        _call.setOperation(_operations[1]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new QName("http://www.escidoc.de/", "validateItemXml"));
        setRequestHeaders(_call);
        setAttachments(_call);
        try
        {
            Object _resp = _call.invoke(new Object[] { in0 });
            if (_resp instanceof java.rmi.RemoteException)
            {
                throw (java.rmi.RemoteException)_resp;
            }
            else
            {
                extractAttachments(_call);
                try
                {
                    return (String)_resp;
                }
                catch (Exception _exception)
                {
                    return (String)JavaUtils.convert(_resp, String.class);
                }
            }
        }
        catch (AxisFault axisFaultException)
        {
            throw axisFaultException;
        }
    }

    public String validateItemXmlBySchema(String in0, String in1, String in2) throws java.rmi.RemoteException
    {
        if (super.cachedEndpoint == null)
        {
            throw new NoEndPointException();
        }
        Call _call = createCall();
        _call.setOperation(_operations[2]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new QName("http://www.escidoc.de/", "validateItemXmlBySchema"));
        setRequestHeaders(_call);
        setAttachments(_call);
        try
        {
            Object _resp = _call.invoke(new Object[] { in0, in1 });
            if (_resp instanceof java.rmi.RemoteException)
            {
                throw (java.rmi.RemoteException)_resp;
            }
            else
            {
                extractAttachments(_call);
                try
                {
                    return (String)_resp;
                }
                catch (Exception _exception)
                {
                    return (String)JavaUtils.convert(_resp, String.class);
                }
            }
        }
        catch (AxisFault axisFaultException)
        {
            throw axisFaultException;
        }
    }

    public void refreshValidationSchemaCache() throws java.rmi.RemoteException
    {
        if (super.cachedEndpoint == null)
        {
            throw new NoEndPointException();
        }
        Call _call = createCall();
        _call.setOperation(_operations[3]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new QName("http://www.escidoc.de/", "refreshValidationSchemaCache"));
        setRequestHeaders(_call);
        setAttachments(_call);
        try
        {
            Object _resp = _call.invoke(new Object[] {});
            if (_resp instanceof java.rmi.RemoteException)
            {
                throw (RemoteException)_resp;
            }
            extractAttachments(_call);
        }
        catch (AxisFault axisFaultException)
        {
            throw axisFaultException;
        }
    }
}
