<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:f="http://xmlns.jcp.org/jsf/core" xmlns:h="http://xmlns.jcp.org/jsf/html" xmlns:ui="http://xmlns.jcp.org/jsf/facelets" xmlns:p="http://primefaces.org/ui" xmlns:pt="http://xmlns.jcp.org/jsf/passthrough">

<head>
    <title>SWORD Depositing Service</title>
</head>

<body bgcolor="white">
    <f:view locale="#{InternationalizationHelper.userLocale}">
        <f:loadBundle var="lbl" basename="Label" />
        <f:loadBundle var="msg" basename="Messages" />
        <f:loadBundle var="tip" basename="Tooltip" />
        <h1>SWORD Depositing Service</h1>
        <p>
            The SWORD Depositing Service is an interface for depositing items from external servers into the eSciDoc repository.<br />
        </p>
        <p>
            <a href="http://colab.mpdl.mpg.de/mediawiki/PubMan_Sword">[more]</a>
        </p>
        <div>
            <h2>Retrieve the Servicedocument:</h2>
            <p>The SWORD servicedocument describes the collections a user can deposit to. (User credentials have to be provided)</p>
            <p>
                Attributes which can be set in the http header: <br /> <b>sword:verbose</b> - Sets the verbose output<br /> <b>sword:noOp</b> - Sets the test deposit option (item will be processed but not saved)<br />
            </p>
            <p style="background-color: #E0EEEE;">
                <b>Exemplary Servicedocument:</b> <br />
                <br /> &#60;service&#62; <br /> &#160; &#60;sword:level&#62;0&#60;/sword:level&#62; <br /> &#160; &#60;sword:verbose&#62;false&#60;/sword:verbose&#62; <br /> &#160; &#60;sword:noOp&#62;true&#60;/sword:noOp&#62; <br /> &#160;&#160; &#60;workspace&#62; <br /> &#160;&#160;&#160; &#60;atom:title type="text"&#62;PubMan SWORD Workspace&#60;/atom:title&#62; <br /> &#160;&#160;&#160;&#160; &#60;collection href="escidoc:123"&#62; <br /> &#160;&#160;&#160;&#160; &#60;atom:title type="text"&#62;Test SWORD Deposit&#60;/atom:title&#62; <br /> &#160;&#160;&#160;&#160; &#60;accept&#62;application/zip&#60;/accept&#62; <br /> &#160;&#160;&#160;&#160; &#60;sword:collectionPolicy&#62;Simple work flow&#60;/sword:collectionPolicy&#62; <br /> &#160;&#160;&#160;&#160; &#60;dcterms:abstract&#62;This is a test collection for SWORD depositing. The policy is: no policy at this time.&#60;/dcterms:abstract&#62; <br /> &#160;&#160;&#160;&#160; &#60;sword:mediation&#62;false&#60;/sword:mediation&#62; <br /> &#160;&#160;&#160;&#160; &#60;sword:treatment&#62;Zip archives recognised as content packages are opened and the individual files contained in them are stored.&#60;/sword:treatment&#62; <br /> &#160;&#160;&#160;&#160; &#60;sword:acceptPackaging &#62;http://purl.org/net/sword-types/tei/peer&#60;/sword:acceptPackaging &#62; <br /> &#160;&#160;&#160;&#160; &#60;sword:acceptPackaging &#62;http://purl.org/escidoc/metadata/schemas/0.1/publication &#60;/sword:acceptPackaging &#62; <br /> &#160;&#160;&#160;&#160; &#60;sword:acceptPackaging &#62;bibTex&#60;/sword:acceptPackaging &#62; <br /> &#160;&#160;&#160;&#160; &#60;sword:acceptPackaging &#62;EndNote&#60;/sword:acceptPackaging &#62; <br /> &#160;&#160;&#160; &#60;/collection&#62; <br /> &#160;&#160; &#60;/workspace&#62; <br /> &#160; &#60;/service&#62; <br />
            </p>
            <p>
                You already have an eSciDoc account for PubMan? Check out your Servicedocument <a href="/pubman/faces/sword-app/servicedocument" target="_blank"> here.</a>
            </p>
        </div>
        <div>
            <h2>Deposit data to PubMan:</h2>
            <p>
                One can <a href="/pubman/faces/sword-app/deposit?collection=" target="_blank"> deposit</a> publication data to PubMan by calling the deposit servlet. (User credentials and collection identifier have to be provided)
            </p>
            <p>
                For detailed information about parameters, error codes etc. please check out the <a href="http://colab.mpdl.mpg.de/mediawiki/PubMan_Sword">PubMan
					SWORD description in CoLab.</a>
            </p>
        </div>
    </f:view>
</body>

</html>