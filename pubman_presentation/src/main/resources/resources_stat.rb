#!/usr/bin/env ruby
# $Id$

require "pp"

IGNORE_UNTRANSLATED = {
   "ae" => nil,
   "oe" => nil,
   "ss" => nil,
   "ue" => nil,
   "faq" => "FAQ",
   "AffiliationTree_txtFax" => "Fax",
   "affiliation_detail_fax" => "Fax",
   "AffiliationTree_txtEmail" => "E-Mail",
   "affiliation_detail_email" => "E-Mail",
   "ENUM_ALTERNATIVETITLETYPE_MATHML" => "MathML",
   /^ENUM_CONTENTCATEGORY_\w+$/ => /^Deprecated: /,
   "ENUM_IDENTIFIERTYPE_ARXIV" => "arXiv",
   "ENUM_IDENTIFIERTYPE_BMC" => "BMC",
   "ENUM_IDENTIFIERTYPE_CONE" => "CoNE",
   "ENUM_IDENTIFIERTYPE_DOI" => "DOI",
   "ENUM_IDENTIFIERTYPE_EDOC" => "eDoc",
   "ENUM_IDENTIFIERTYPE_ESCIDOC" => "eSciDoc",
   "ENUM_IDENTIFIERTYPE_ISBN" => "ISBN",
   "ENUM_IDENTIFIERTYPE_ISI" => "ISI",
   "ENUM_IDENTIFIERTYPE_ISSN" => "ISSN",
   "ENUM_IDENTIFIERTYPE_PII" => "PII",
   "ENUM_IDENTIFIERTYPE_PMC" => "PMC",
   "ENUM_IDENTIFIERTYPE_PMID" => "PMID",
   "ENUM_IDENTIFIERTYPE_PND" => "PND",
   "ENUM_IDENTIFIERTYPE_SSRN" => "SSRN",
   "ENUM_IDENTIFIERTYPE_URI" => "URI",
   "ENUM_IDENTIFIERTYPE_URN" => "URN",
   "ENUM_IDENTIFIERTYPE_ZDB" => "ZDB",
   "ENUM_LOGICOPTIONS_LOGIC_AND" => "AND",
   "ENUM_LOGICOPTIONS_LOGIC_NOT" => "NOT",
   "ENUM_LOGICOPTIONS_LOGIC_OR" => "OR",
   /^ENUM_MIMETYPE_\w+$/ => nil,
   /^ENUM_LANGUAGE_[A-Z][A-Z]$/ => nil,
   "EditItem_NO_ITEM_SET" => "-",
   "EditItem_lblDDCSubject" => "DDC",
   "Export_ExportFormat_AJP" => "AJP",
   "Export_ExportFormat_APA" => "APA",
   "Export_ExportFormat_GFZPUBLISTS" => "GFZPublists-Citation",
   "Export_ExportFormat_BIBTEX" => "BibTeX",
   "Export_ExportFormat_ENDNOTE" => "EndNote (UTF-8)",
   "Export_ExportFormat_XML" => "XML",
   "Export_ExportFormat_ESCIDOC_XML" => "eSciDoc XML",
   "Export_ExportFormat_JUS" => "JUS",
   "Export_ExportFormat_XML" => "xml",
   "Export_ExportFormat_DEFAULT" => "DEFAULT",
   "Export_ExportFormat_TEST" => "TEST",
   "Export_FileFormat_HTML" => "html",
   "Export_FileFormat_ODT" => "odt",
   "Export_FileFormat_PDF" => "pdf",
   "Export_FileFormat_RTF" => "rtf",
   "Pubman_browserTitle" => "PubMan",
   /^ViewItem(Full|Medium)_lblFileSizeB$/ => "B",
   /^ViewItem(Full|Medium)_lblFileSizeKB$/ => "KB",
   /^ViewItem(Full|Medium)_lblFileSizeMB$/ => "MB",
   "ViewItemFull_lblNoEntry" => "-",
   "ViewItem_lkAPA" => "APA",
   "ViewItem_lkBIBTEX" => "BibTeX",
   "ViewItem_lkENDNOTE" => "EndNote",
   "ViewItem_lkESCIDOC" => "eSciDoc",
   "easy_submission_lblIDTypeArxiv" => "arXiv ID",
   "easy_submission_lblIDTypeEscidoc" => "eSciDoc ID",
   "easy_submission_lblLocatorUrl" => "URL",
   "export_btEMail" => "E-Mail",
   /^export_btn?Export$/ => "Ok",
   "lbl_noEntry" => "-",
   "no" => "No",
   "ok" => "Ok",
   "openSearch_shortDesc" => "PubMan",
   "title" => "Publication Manager",
   "multipleImport_checkboxSomething" => "multipleImport_checkboxSomething",
   "ViewItemFull_lblNoAccess" => "(No access)",
   "ViewItemFull_lblRelation" => "Relation",
   "ViewItemFull_lblSocialBookmarkingDescription" => "eSciDoc PubMan:",
   "EditItem_lblDateFormat" => "(YYYY-MM-DD)",
   "EditItem_lblDimension" => "KB",
   "EditItem_lblURL" => "URL",
   "ViewItem_lblRelation1" => "Relation:",
   "ViewItemFull_lblDDCSubject" => "DDC",
   "Tools_lblCoNE" => "CoNE",
   "Tools_lblColab" => "http://colab.mpdl.mpg.de/mediawiki/ESciDoc_Interfaces",
   "Tools_lblREST" => "REST",
   "Tools_lblUnAPI" => "unAPI",

   # Imeji
   "NOT" => "NOT",
   "HomePage" => "imeji",
   "email" => "Email",

}

def parse_resource_file( filename )
   resource = {}
   open( filename ) do |io|
      io.each do |line|
         next if line =~ /^\s*#/
         next if line =~ /^\s*$/
         if line =~ /^\s*([\w\.\-\(\)]+)\s*=\s*(.+)$/
	    key, val = $1, $2
            if resource[ $1 ]
               puts "#{filename}:#{$.}: Warning: duplicate resource: #{ key }"
            end
            # val.gsub!( /[\[\(](new|neu)[\]\)]/io, "" )
	    val.strip!
	    if val.empty? or val == "\\"
	       puts "#{filename}:#{$.}: Warning: empty value: #{ key }"
	    end
            resource[ key ] = val
         else
            puts "#{filename}:#{$.}: Warining: unknown line: #{line}"
         end
      end
   end
   resource
end

if $0 == __FILE__
   opt_tsv = false
   if ARGV[0] and ARGV[0] =~ /\A-tsv/ # output tsv file.
      ARGV.shift
      opt_tsv = true
   end
   puts "[Usage]  #{$0} resource-files ..." if ARGV.empty?
   ARGV.each do |f|
      if f =~ /(\w+)_([a-z][a-z])\.properties$/ and $2 != "en"
         lang = $2
         count = {
            "only in en" => [],
            "only in #{lang}" => [],
            "untranslated" => [],
            "translated" => 0
         }
         orig_file = f.gsub( /_([a-z][a-z])\.properties$/, '_en.properties' )
         orig = parse_resource_file( orig_file )
         new  = parse_resource_file( f )
         ( orig.keys + new.keys ).uniq.sort.each do |k|
            if orig[ k ]
               if not new[ k ]
                  count[ "only in en" ] << k
               elsif orig[ k ] == new[ k ]
                  if IGNORE_UNTRANSLATED.keys.find{|e|
                        e === k and
                        ( IGNORE_UNTRANSLATED[e].nil? or
                          IGNORE_UNTRANSLATED[e] === orig[k] )
                     }
                     count[ "translated" ] += 1
                  else
                     count[ "untranslated" ] << k
                  end
               elsif k == new[ k ]
               	  count[ "untranslated" ] << k
               elsif new[ k ] =~ /[\[\(](new|neu)[\]\)]/io
               	  count[ "untranslated" ] << k
               else
                  count[ "translated" ] += 1
               end
            else
               count[ "only in #{lang}" ] << k
            end
            puts [ k, orig[k], new[k] ].join( "\t" ) if opt_tsv
         end
         if not opt_tsv
            pp count
            puts "#{f} total: #{ "%.02f%%" % ( 100 * count["translated"].to_f / orig.keys.size ) } (#{count["translated"]}/#{orig.keys.size})"
         end
      else
         puts "Skip #{f}"
      end
   end
end
