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
* Copyright 2006-2009 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/

/**
 * ItemValidatingWebServiceServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */
package de.mpg.escidoc.services.validation.webservice;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.Remote;
import java.util.HashSet;

import javax.xml.namespace.QName;
import javax.xml.rpc.ServiceException;

import org.apache.axis.AxisFault;
import org.apache.axis.EngineConfiguration;
import org.apache.axis.client.Service;
import org.apache.axis.client.Stub;

public class ItemValidatingWebServiceServiceLocator extends Service implements ItemValidatingWebServiceService
{
    public ItemValidatingWebServiceServiceLocator()
    {
    }

    public ItemValidatingWebServiceServiceLocator(EngineConfiguration config)
    {
        super(config);
    }

    public ItemValidatingWebServiceServiceLocator(java.lang.String wsdlLoc, QName sName) throws ServiceException
    {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for validation
    private String validation_address = "http://localhost:8080/validation_service/services/validation";

    public String getvalidationAddress()
    {
        return validation_address;
    }

    // The WSDD service name defaults to the port name.
    private String validationWSDDServiceName = "validation";

    public String getvalidationWSDDServiceName()
    {
        return validationWSDDServiceName;
    }

    public void setvalidationWSDDServiceName(String name)
    {
        validationWSDDServiceName = name;
    }

    public ItemValidatingWebService getvalidation() throws ServiceException
    {
        URL endpoint;
        try
        {
            endpoint = new URL(validation_address);
        }
        catch (MalformedURLException e)
        {
            throw new ServiceException(e);
        }
        return getvalidation(endpoint);
    }

    public ItemValidatingWebService getvalidation(URL portAddress) throws ServiceException
    {
        try
        {
            ValidationSoapBindingStub _stub = new ValidationSoapBindingStub(portAddress, this);
            _stub.setPortName(getvalidationWSDDServiceName());
            return _stub;
        }
        catch (AxisFault e)
        {
            return null;
        }
    }

    public void setvalidationEndpointAddress(String address)
    {
        validation_address = address;
    }

    /**
     * For the given interface, get the stub implementation. If this service has no port for the given interface, then
     * ServiceException is thrown.
     */
    public Remote getPort(Class serviceEndpointInterface) throws ServiceException
    {
        try
        {
            if (ItemValidatingWebService.class.isAssignableFrom(serviceEndpointInterface))
            {
                ValidationSoapBindingStub _stub = new ValidationSoapBindingStub(new URL(validation_address), this);
                _stub.setPortName(getvalidationWSDDServiceName());
                return _stub;
            }
        }
        catch (Throwable t)
        {
            throw new ServiceException(t);
        }
        throw new ServiceException("There is no stub implementation for the interface:  "
                + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation. If this service has no port for the given interface, then
     * ServiceException is thrown.
     */
    public Remote getPort(QName portName, Class serviceEndpointInterface) throws ServiceException
    {
        if (portName == null)
        {
            return getPort(serviceEndpointInterface);
        }
        String inputPortName = portName.getLocalPart();
        if ("validation".equals(inputPortName))
        {
            return getvalidation();
        }
        else
        {
            Remote _stub = getPort(serviceEndpointInterface);
            ((Stub)_stub).setPortName(portName);
            return _stub;
        }
    }

    public QName getServiceName()
    {
        return new QName("http://www.escidoc.de/", "ItemValidatingWebServiceService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts()
    {
        if (ports == null)
        {
            ports = new HashSet();
            ports.add(new QName("http://www.escidoc.de/", "validation"));
        }
        return ports.iterator();
    }

    /**
     * Set the endpoint address for the specified port name.
     */
    public void setEndpointAddress(String portName, String address) throws ServiceException
    {
        if ("validation".equals(portName))
        {
            setvalidationEndpointAddress(address);
        }
        else
        { // Unknown Port Name
            throw new ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
     * Set the endpoint address for the specified port name.
     */
    public void setEndpointAddress(QName portName, String address) throws ServiceException
    {
        setEndpointAddress(portName.getLocalPart(), address);
    }
}
