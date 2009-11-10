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
package de.mpg.escidoc.pubman.test.gui.modules.item;

import java.util.ArrayList;
import java.util.List;

/**
 * @author endres
 * 
 */
public class PubmanItem
{
    public enum ItemType {
        Item,
        ItemWithFile,
        ItemWithLocator,
        ItemWithFileAndLocator
    }
    
    public enum GenreType {
        ARTICLE, 
        BOOK, 
        BOOK_ITEM, 
        PROCEEDINGS, 
        CONFERENCE_PAPER, 
        TALK_AT_EVENT, 
        CONFERENCE_REPORT, 
        POSTER, 
        COURSEWARE_LECTURE, 
        THESIS, 
        PAPER, 
        REPORT, 
        ISSUE, 
        JOURNAL, 
        MANUSCRIPT, 
        SERIES, 
        OTHER
    }
    
    public enum ComponentVisibility {
        PUBLIC,
        PRIVATE,
        /** aka restricted */
        AUDIENCE
    }
    
    public enum CreatorRole {
        ARTIST,
        AUTHOR,
        EDITOR,
        PAINTER,
        ILLUSTRATOR,
        PHOTOGRAPHER,
        COMMENTATOR,
        TRANSCRIBER,
        ADVISOR,
        TRANSLATOR,
        CONTRIBUTOR
    }
    public enum CreatorType {
        Person,
        Organization
    }
    
    public enum IdentifierType {
        URI,
        ISBN,
        ISSN,
        DOI,
        URN,
        PII,
        EDOC,
        ESCIDOC,
        ISI,
        PND,
        ZDB,
        PMID,
        ARXIV,
        PMC,
        BMC,
        OTHER
   }
      
    public ItemType type = null;
    public GenreType genre = null;
    
    List<PubmanItemBasic> basicList = new ArrayList<PubmanItemBasic>();
    List<PubmanItemFile> fileList = new ArrayList<PubmanItemFile>();
    List<PubmanItemFileLocators> locatorsList = new ArrayList<PubmanItemFileLocators>();
    List<PubmanItemPersonOrganizations> personOrganizationList = new ArrayList<PubmanItemPersonOrganizations>();
    List<PubmanItemContent> contentList = new ArrayList<PubmanItemContent>();
    List<PubmanItemDetails> detailsList = new ArrayList<PubmanItemDetails>();
    List<PubmanItemEvent> eventList = new ArrayList<PubmanItemEvent>();
    List<PubmanItemSource> sourceList = new ArrayList<PubmanItemSource>();
    
    
    public PubmanItem( ItemType type, GenreType genre ) {
        this.type = type;
        this.genre = genre;
    }
    
    public void addBasic( PubmanItemBasic basic ) {
        this.basicList.add(basic);
    }
    public void addFile( PubmanItemFile file ) {
        this.fileList.add( file );
    }
    public void addFileLocators( PubmanItemFileLocators fileLoc ) {
        this.locatorsList.add( fileLoc );
    }
    public void addPersonOrganisation( PubmanItemPersonOrganizations persOrg ) {
        this.personOrganizationList.add( persOrg );
    }
    public void addContent( PubmanItemContent content ) {
        this.contentList.add( content );
    }
    public void addDetails( PubmanItemDetails details ) {
        this.detailsList.add( details );
    }
    public void addEvent( PubmanItemEvent event ) {
        this.eventList.add( event );
    }
    public void addSource( PubmanItemSource source ) {
        this.sourceList.add( source );
    }

    public ItemType getType()
    {
        return type;
    }

    public String getGenre()
    {
        return genre.toString();
    }

    public List<PubmanItemBasic> getBasicList()
    {
        return basicList;
    }

    public List<PubmanItemFile> getFileList()
    {
        return fileList;
    }

    public List<PubmanItemFileLocators> getLocatorsList()
    {
        return locatorsList;
    }

    public List<PubmanItemPersonOrganizations> getPersonOrganizationList()
    {
        return personOrganizationList;
    }

    public List<PubmanItemContent> getContentList()
    {
        return contentList;
    }

    public List<PubmanItemDetails> getDetailsList()
    {
        return detailsList;
    }

    public List<PubmanItemEvent> getEventList()
    {
        return eventList;
    }

    public List<PubmanItemSource> getSourceList()
    {
        return sourceList;
    }
    
    int getBasicSize() {
        return this.basicList.size();
    }
    int getFileSize() {
        return this.fileList.size();
    }
    int getDetailsSize() {
        return this.detailsList.size();
    }
    
}
