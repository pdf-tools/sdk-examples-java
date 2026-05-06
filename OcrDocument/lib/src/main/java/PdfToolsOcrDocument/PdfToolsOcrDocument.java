/****************************************************************************
 *
 * File:            pdftoolsocrdocument.java
 *
 * Usage:           java pdftoolsocrdocument <ocrEngineName> <language> <inputPath> <outputPath>
 *                  
 * Title:           OCR a PDF document
 *                  
 * Description:     Apply OCR to a PDF document to make scanned content
 *                  searchable. Text is recognized from images, existing text
 *                  is updated with correct Unicode, and tagging is added for
 *                  accessibility.
 *                  
 * Author:          PDF Tools AG
 *
 * Copyright:       Copyright (C) 2026 PDF Tools AG, Switzerland
 *                  Permission to use, copy, modify, and distribute this
 *                  software and its documentation for any purpose and without
 *                  fee is hereby granted, provided that the above copyright
 *                  notice appear in all copies and that both that copyright
 *                  notice and this permission notice appear in supporting
 *                  documentation. This software is provided "as is" without
 *                  express or implied warranty.
 *
 ***************************************************************************/

package PdfToolsOcrDocument;

import java.util.stream.Collectors;

import java.io.File;
import java.util.EnumSet;
import com.pdftools.sys.FileStream;
import com.pdftools.Sdk;
import com.pdftools.pdf.Document;
import com.pdftools.ocr.Engine;
import com.pdftools.ocr.Processor;
import com.pdftools.ocr.OcrOptions;
import com.pdftools.ocr.ImageProcessingMode;
import com.pdftools.ocr.TextProcessingMode;
import com.pdftools.ocr.TextSkipMode;
import com.pdftools.ocr.UnicodeSource;
import com.pdftools.ocr.PageProcessingMode;
import com.pdftools.ocr.TaggingMode;

public class PdfToolsOcrDocument
{
    static void usage() {
        System.out.println("Usage: java pdftoolsocrdocument <ocrEngineName> <language> <inputPath> <outputPath>");
    }

    public static void main(String[] args)
    {
        // Check command line parameters
        if (args.length < 4 || args.length > 4) {
            usage();
            return;
        }

        try
        {
            // By default, a test license key is active. In this case, a watermark is added to the output. 
            // If you have a license key, please uncomment the following call and set the license key.
            // Sdk.initialize("insert-license-key-here");

            ocrDocument(args[0], args[1], args[2], args[3]);

            System.out.println("Execution successful.");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void ocrDocument(String ocrEngineName, String language, String inPath, String outPath) throws Exception
    {
        // Create the OCR engine
        try (Engine engine = Engine.create(ocrEngineName)) {

            // Set the language(s) for OCR recognition (e.g. "German,English")
            engine.setLanguages(language);

            // Open input document
            try (
                FileStream inStr = new FileStream(inPath, FileStream.Mode.READ_ONLY);
                Document inDoc = Document.open(inStr)) {

                // Configure OCR options
                OcrOptions options = new OcrOptions();

                // Configure image OCR: recognize text from scanned images
                options.getImageOptions().setMode(ImageProcessingMode.UPDATE_TEXT);
                options.getImageOptions().setRemoveOnlyInvisibleOcrText(true);
                options.getImageOptions().setDeskewScan(true);
                options.getImageOptions().setRotateScan(true);

                // Configure text OCR: update non-extractable text with correct Unicode
                options.getTextOptions().setMode(TextProcessingMode.UPDATE);
                options.getTextOptions().setSkipMode(EnumSet.of(TextSkipMode.KNOWN_SYMBOLIC));
                options.getTextOptions().setUnicodeSource(EnumSet.of(UnicodeSource.INSTALLED_FONT));

                // Configure page OCR: process all pages and add tagging for accessibility
                options.getPageOptions().setMode(PageProcessingMode.ALL);
                options.getPageOptions().setTagging(TaggingMode.AUTO);

                // Create the OCR processor and add a warning handler
                Processor processor = new Processor();
                processor.addWarningListener(new Processor.WarningListener() {
                    @Override
                    public void warning(Processor.Warning event) {
                        System.out.println(String.format("- %s: %s (%s%s)",
                            event.getCategory(), event.getMessage(), event.getContext(),
                            event.getPageNo() > 0 ? " page " + event.getPageNo() : ""));
                    }
                });

                // Create stream for output file
                try (FileStream outStr = new FileStream(outPath, FileStream.Mode.READ_WRITE_NEW)) {

                    // Process the document with OCR
                    try (Document outDoc = processor.process(inDoc, engine, outStr, options, null)) {
                    }
                }
            }
        }
    }
}
