/**
 * Project: taurus-agent
 * 
 * File Created at 2013-2-26
 * $Id$
 * 
 * Copyright 2013 dianping.com.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Dianping Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with dianping.com.
 */
package com.dp.bigdata.taurus.agent.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * TODO Comment of FileExtarctUtils
 * @author renyuan.sun
 *
 */
public class FileExtractUtils {
    private static final Log LOG = LogFactory.getLog(FileExtractUtils.class);
    
    /** Untar an input file into an output file.

     * The output file is created in the output folder, having the same name
     * as the input file, minus the '.tar' extension. 
     * 
     * @param inputFile     the input .tar file
     * @param outputDir     the output directory file. 
     * @throws IOException 
     * @throws FileNotFoundException
     *  
     * @return  The {@link List} of {@link File}s with the untared content.
     * @throws ArchiveException 
     */
    public static List<File> unTar(final File inputFile, final File outputDir) throws FileNotFoundException, IOException, ArchiveException {
        LOG.debug(String.format("Untaring %s to dir %s.", inputFile.getAbsolutePath(), outputDir.getAbsolutePath()));
        final List<File> untaredFiles = new LinkedList<File>();
        final InputStream is = new FileInputStream(inputFile); 
        final TarArchiveInputStream debInputStream = (TarArchiveInputStream) new ArchiveStreamFactory().createArchiveInputStream("tar", is);
        TarArchiveEntry entry = null; 
        while ((entry = (TarArchiveEntry)debInputStream.getNextEntry()) != null) {
            final File outputFile = new File(outputDir, entry.getName());
            if (entry.isDirectory()) {
                if (!outputFile.exists()) {
                    if (!outputFile.mkdirs()) {
                        throw new IllegalStateException(String.format("Couldn't create directory %s.", outputFile.getAbsolutePath()));
                    }
                }
            } else {
                final OutputStream outputFileStream = new FileOutputStream(outputFile); 
                IOUtils.copy(debInputStream, outputFileStream);
                outputFileStream.close();
            }
            untaredFiles.add(outputFile);
        }
        debInputStream.close(); 
        return untaredFiles;
    }
    
    /**
     * Unbzip an input file into an output file.
     * <p>
     * The output file is created in the output folder, having the same name
     * as the input file, minus the '.bz2' extension. 
     * 
     * @param inputFile     the input .bz2 file
     * @param outputDir     the output directory file. 
     * @throws IOException 
     * @throws FileNotFoundException
     *  
     * @return  The {@File} with the unbzipped content.
     */
    public static File unBzip(final File inputFile, final File outputDir) throws FileNotFoundException, IOException {

        LOG.debug(String.format("Unbzipping %s to dir %s.", inputFile.getAbsolutePath(), outputDir.getAbsolutePath()));

        final File outputFile = new File(outputDir, inputFile.getName().substring(0, inputFile.getName().length() - 4));

        final BZip2CompressorInputStream in = new BZip2CompressorInputStream(new FileInputStream(inputFile));
        final FileOutputStream out = new FileOutputStream(outputFile);

        IOUtils.copy(in, out);
        in.close();
        out.close();

        return outputFile;
    }
    
   
    /**
     * Ungzip an input file into an output file.
     * <p>
     * The output file is created in the output folder, having the same name
     * as the input file, minus the '.gz' extension. 
     * 
     * @param inputFile     the input .gz file
     * @param outputDir     the output directory file. 
     * @throws IOException 
     * @throws FileNotFoundException
     *  
     * @return  The {@File} with the ungzipped content.
     */
    public static File unGzip(final File inputFile, final File outputDir) throws FileNotFoundException, IOException {

        LOG.debug(String.format("Ungzipping %s to dir %s.", inputFile.getAbsolutePath(), outputDir.getAbsolutePath()));

        final File outputFile = new File(outputDir, inputFile.getName().substring(0, inputFile.getName().length() - 3));

        final GZIPInputStream in = new GZIPInputStream(new FileInputStream(inputFile));
        final FileOutputStream out = new FileOutputStream(outputFile);

        IOUtils.copy(in, out);
        in.close();
        out.close();

        return outputFile;
    }
    
   
    /**
     * Unzip an input file into an output file.
     * <p>
     * The output file is created in the output folder, having the same name
     * as the input file, minus the '.zip' extension. 
     * 
     * @param inputFile     the input .zip file
     * @param outputDir     the output directory file. 
     * @throws IOException 
     * @throws FileNotFoundException
     *  
     * @return  The {@File} with the ungzipped content.
     */
    public static void unZip(final File inputFile, final File outputDir) throws FileNotFoundException, IOException {
        LOG.debug(String.format("Unzipping %s to dir %s.", inputFile.getAbsolutePath(), outputDir.getAbsolutePath()));

        byte[] buf = new byte[1024];
        ZipInputStream zipinputstream = null;
        ZipEntry zipentry;
        zipinputstream = new ZipInputStream(
                new FileInputStream(inputFile));

        zipentry = zipinputstream.getNextEntry();
        while (zipentry != null) {
            //for each entry to be extracted
            String entryName = outputDir + "/" + zipentry.getName();
            entryName = entryName.replace('/', File.separatorChar);
            entryName = entryName.replace('\\', File.separatorChar);
            int n;
            FileOutputStream fileoutputstream;
            File newFile = new File(entryName);
            
            if (zipentry.isDirectory()) {
                if (!newFile.mkdirs()) {
                    break;
                }
                zipentry = zipinputstream.getNextEntry();
                continue;
            }
            fileoutputstream = new FileOutputStream(entryName);

            while ((n = zipinputstream.read(buf, 0, 1024)) > -1) {
                fileoutputstream.write(buf, 0, n);
            }

            fileoutputstream.close();
            zipinputstream.closeEntry();
            zipentry = zipinputstream.getNextEntry();

        }//while
        zipinputstream.close();
    }
    
  
    
    public static List<File> unTar(final File inputFile) throws FileNotFoundException, IOException, ArchiveException {
        return unTar(inputFile,inputFile.getParentFile());
    }
    
    public static File unBzip(final File inputFile) throws FileNotFoundException, IOException {
        return unBzip(inputFile,inputFile.getParentFile());
    }
    
    public static File unGzip(final File inputFile) throws FileNotFoundException, IOException {
        return unGzip(inputFile,inputFile.getParentFile());
    }
      
    public static void unZip(final File inputFile) throws FileNotFoundException, IOException {
        unZip(inputFile,inputFile.getParentFile());
    }
}
