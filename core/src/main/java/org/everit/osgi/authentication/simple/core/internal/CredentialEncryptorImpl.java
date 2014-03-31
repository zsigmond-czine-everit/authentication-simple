/**
 * This file is part of org.everit.osgi.authentication.simple.core.
 *
 * org.everit.osgi.authentication.simple.core is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * org.everit.osgi.authentication.simple.core is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with org.everit.osgi.authentication.simple.core.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.everit.osgi.authentication.simple.core.internal;

import java.security.MessageDigest;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.everit.osgi.authentication.simple.core.CredentialEncryptor;

public class CredentialEncryptorImpl implements CredentialEncryptor {

    private static final String PLAIN = "{plain}";

    private final MessageDigest messageDigest;

    public CredentialEncryptorImpl(final MessageDigest messageDigest) {
        super();
        this.messageDigest = messageDigest;
    }

    @Override
    public boolean checkCredential(final String plainCredential, final String encryptedCredential) {
        if (plainCredential == null) {
            throw new IllegalArgumentException("plainCredential cannot be null");
        }
        if (encryptedCredential == null) {
            throw new IllegalArgumentException("encryptedCredential cannot be null");
        }
        if (encryptedCredential.startsWith(PLAIN)) {
            return encryptedCredential.equals(PLAIN + plainCredential);
        }
        return encryptedCredential.equals(encryptCredential(plainCredential));
    }

    @Override
    public String encryptCredential(final String plainCredential) {
        if (plainCredential == null) {
            throw new IllegalArgumentException("plainCredential cannot be null");
        }
        byte[] bytesOfPlainCredential = StringUtils.getBytesUtf8(plainCredential);
        byte[] digest = messageDigest.digest(bytesOfPlainCredential);
        return format(digest);
    }

    private String format(final byte[] digest) {
        byte[] base64Digest = Base64.encodeBase64(digest);
        String base64DigestString = StringUtils.newStringUtf8(base64Digest);
        return base64DigestString;
    }

}
