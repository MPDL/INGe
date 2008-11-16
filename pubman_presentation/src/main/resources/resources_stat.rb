#!/usr/bin/env ruby
# $Id$

require "pp"

IGNORE_UNTRANSLATED = [ "ae", "oe", "ss", "ue" ]

def parse_resource_file( filename )
   resource = {}
   open( filename ) do |io|
      io.each do |line|
         next if line =~ /^\s*#/
         next if line =~ /^\s*$/
         if line =~ /^\s*([\w\.]+)\s*=\s*(.+)$/
            if resource[ $1 ]
               puts "#{filename}:#{$.}: Warining: duplicate resource: #{ $1 }"
            end
            resource[ $1 ] = $2.strip
         else
            puts "#{filename}:#{$.}: Warining: unknown line: #{line}"
         end
      end
   end
   resource
end

if $0 == __FILE__
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
               elsif orig[ k ] == new[ k ] and not IGNORE_UNTRANSLATED.include? k
                  count[ "untranslated" ] << k
               else
                  count[ "translated" ] += 1
               end
            else
               count[ "only in #{lang}" ] << k
            end
         end
         pp count
         count.each do |k, v|
            # pp k
         end
         puts "#{f} total: #{ "%.02f%%" % ( 100 * count["translated"].to_f / orig.keys.size ) } (#{count["translated"]}/#{orig.keys.size})"
      else
         puts "Skip #{f}"
      end
   end
end
