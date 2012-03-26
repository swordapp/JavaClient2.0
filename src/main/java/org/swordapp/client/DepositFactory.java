/*
 * Copyright (c) 2011, Richard Jones, Cottage Labs
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the <organization> nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.swordapp.client;

import java.io.InputStream;

/**
 * This is a convenience class to allow you to construct Deposit objects for the
 * various deposit operations which only take the arguments which are relevant to
 * that operation.  It's just Syntactic Sugar, you don't have to use it.
 */
public class DepositFactory
{
	// Deposit(EntryPart entryPart, InputStream file, String filename, String mimeType, String packaging,
    //          String slug, String md5, boolean inProgress, boolean metadataRelevant)

    /**
     * Create a Deposit object for creating a new object by placing just an Entry/Metadata
     * document on the server
     *
     * @param entryPart
     * @param slug
     * @param inProgress
     * @return
     */
	public Deposit newMetadataOnly(EntryPart entryPart, String slug, boolean inProgress)
	{
		return new Deposit(entryPart, null, null, null, null, slug, null, inProgress, false);
	}

    /**
     * Create a Deposit object for creating a new object by placing just an Entry/Metadata
     * document on the server
     *
     * @param entryPart
     * @return
     */
	public Deposit newMetadataOnly(EntryPart entryPart)
	{
		return this.newMetadataOnly(entryPart, null, false);
	}

    /**
     * Create a Deposit object for creating a new object by placing a binary package
     * onto the server
     *
     * @param file
     * @param filename
     * @param mimeType
     * @param packaging
     * @param slug
     * @param md5
     * @param inProgress
     * @return
     */
	public Deposit newBinaryOnly(InputStream file, String filename, String mimeType, String packaging, String slug, String md5, boolean inProgress)
	{
		return new Deposit(null, file, filename, mimeType, packaging, slug, md5, inProgress, false);
	}

    /**
     * Create a Deposit object for creating a new object by placing a binary package
     * onto the server
     *
     * @param file
     * @param filename
     * @param mimeType
     * @return
     */
	public Deposit newBinaryOnly(InputStream file, String filename, String mimeType)
	{
		return this.newBinaryOnly(file, filename, mimeType, null, null, null, false);
	}

    /**
     * Create a Deposit object for creating a new object by placing a binary package
     * onto the server
     *
     * @param file
     * @param filename
     * @param mimeType
     * @param packaging
     * @return
     */
	public Deposit newBinaryOnly(InputStream file, String filename, String mimeType, String packaging)
	{
		return this.newBinaryOnly(file, filename, mimeType, packaging, null, null, false);
	}

    /**
     * Create a Deposit object for creating a new object by placing both metadata and
     * binary content (multipart) onto the server
     *
     * @param entryPart
     * @param file
     * @param filename
     * @param mimeType
     * @param packaging
     * @param slug
     * @param md5
     * @param inProgress
     * @return
     */
	public Deposit newMultipart(EntryPart entryPart, InputStream file, String filename, String mimeType, String packaging, String slug, String md5, boolean inProgress)
	{
		return new Deposit(entryPart, file, filename, mimeType, packaging, slug, md5, inProgress, false);
	}

    /**
     * Create a Deposit object for creating a new object by placing both metadata and
     * binary content (multipart) onto the server
     *
     * @param entryPart
     * @param file
     * @param filename
     * @param mimeType
     * @return
     */
	public Deposit newMultipart(EntryPart entryPart, InputStream file, String filename, String mimeType)
	{
		return this.newMultipart(entryPart, file, filename, mimeType, null, null, null, false);
	}

    /**
     * Create a Deposit object for creating a new object by placing both metadata and
     * binary content (multipart) onto the server
     *
     * @param entryPart
     * @param file
     * @param filename
     * @param mimeType
     * @param packaging
     * @return
     */
	public Deposit newMultipart(EntryPart entryPart, InputStream file, String filename, String mimeType, String packaging)
	{
		return this.newMultipart(entryPart, file, filename, mimeType, packaging, null, null, false);
	}

    /**
     * Create a Deposit object for replacing the metadata of an item
     *
     * @param entryPart
     * @param inProgress
     * @return
     */
	public Deposit replaceMetadata(EntryPart entryPart, boolean inProgress)
	{
		return new Deposit(entryPart, null, null, null, null, null, null, inProgress, false);
	}

    /**
     * Create a Deposit object for replacing the metadata of an item
     *
     * @param entryPart
     * @return
     */
	public Deposit replaceMetadata(EntryPart entryPart)
	{
		return this.replaceMetadata(entryPart, false);
	}

	public Deposit replaceBinary(InputStream file, String filename, String mimeType, String packaging, String md5, boolean inProgress, boolean metadataRelevant)
	{
		return new Deposit(null, file, filename, mimeType, packaging, null, md5, inProgress, metadataRelevant);
	}

	public Deposit replaceBinary(InputStream file, String filename, String mimeType, String packaging)
	{
		return this.replaceBinary(file, filename, mimeType, packaging, null, false, false);
	}

	public Deposit replaceBinary(InputStream file, String filename, String mimeType)
	{
		return this.replaceBinary(file, filename, mimeType, null, null, false, false);
	}

	public Deposit replaceMultipart(EntryPart entryPart, InputStream file, String filename, String mimeType, String packaging, String md5, boolean inProgress)
	{
		return new Deposit(entryPart, file, filename, mimeType, packaging, null, md5, inProgress, false);
	}

	public Deposit replaceMultipart(EntryPart entryPart, InputStream file, String filename, String mimeType, String packaging)
	{
		return this.replaceMultipart(entryPart, file, filename, mimeType, packaging, null, false);
	}

	public Deposit replaceMultipart(EntryPart entryPart, InputStream file, String filename, String mimeType)
	{
		return this.replaceMultipart(entryPart, file, filename, mimeType, null, null, false);
	}

	public Deposit addMediaResource(InputStream file, String filename, String mimeType)
	{
		return this.addMediaResource(file, filename, mimeType, null, true);
	}

	public Deposit addMediaResource(InputStream file, String filename, String mimeType, String md5, boolean metadataRelevant)
	{
		return new Deposit(null, file, filename, mimeType, null, null, md5, false, metadataRelevant);
	}

	public Deposit addBinary(InputStream file, String filename, String mimeType, String packaging, String md5, boolean inProgress)
	{
		return new Deposit(null, file, filename, mimeType, packaging, null, md5, inProgress, true);
	}

	public Deposit addBinary(InputStream file, String filename, String mimeType, String packaging)
	{
		return this.addBinary(file, filename,  mimeType, packaging, null, false);
	}

	public Deposit addBinary(InputStream file, String filename, String mimeType)
	{
		return this.addBinary(file, filename, mimeType, null, null, false);
	}

	public Deposit addMetadata(EntryPart entryPart, boolean inProgress)
	{
		return new Deposit(entryPart, null, null, null, null, null, null, inProgress, true);
	}

	public Deposit addMetadata(EntryPart entryPart)
	{
		return this.addMetadata(entryPart, false);
	}

	public Deposit addMultipart(EntryPart entryPart, InputStream file, String filename, String mimeType, String packaging, String md5, boolean inProgress)
	{
		return new Deposit(entryPart, file, filename, mimeType, packaging, null, md5, inProgress, true);
	}

	public Deposit addMultipart(EntryPart entryPart, InputStream file, String filename, String mimeType, String packaging)
	{
		return this.addMultipart(entryPart, file, filename, mimeType, packaging, null, false);
	}

	public Deposit addMultipart(EntryPart entryPart, InputStream file, String filename, String mimeType)
	{
		return this.addMultipart(entryPart, file, filename, mimeType, null, null, false);
	}
}
