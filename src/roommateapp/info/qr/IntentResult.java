/*
 *  Roommate
 *  Copyright (C) 2012,2013 Roommate Team (info@roommateapp.info)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package roommateapp.info.qr;

/*
 * Copyright 2009 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * <p>Encapsulates the result of a barcode scan invoked through {@link IntentIntegrator}.</p>
 *
 * @author Sean Owen
 */
public final class IntentResult {

  private final String contents;
  private final String formatName;
  private final byte[] rawBytes;
  private final Integer orientation;
  private final String errorCorrectionLevel;

  IntentResult() {
    this(null, null, null, null, null);
  }

  IntentResult(String contents,
               String formatName,
               byte[] rawBytes,
               Integer orientation,
               String errorCorrectionLevel) {
    this.contents = contents;
    this.formatName = formatName;
    this.rawBytes = rawBytes;
    this.orientation = orientation;
    this.errorCorrectionLevel = errorCorrectionLevel;
  }

  /**
   * @return raw content of barcode
   */
  public String getContents() {
    return contents;
  }

  /**
   * @return name of format, like "QR_CODE", "UPC_A". See {@code BarcodeFormat} for more format names.
   */
  public String getFormatName() {
    return formatName;
  }

  /**
   * @return raw bytes of the barcode content, if applicable, or null otherwise
   */
  public byte[] getRawBytes() {
    return rawBytes;
  }

  /**
   * @return rotation of the image, in degrees, which resulted in a successful scan. May be null.
   */
  public Integer getOrientation() {
    return orientation;
  }

  /**
   * @return name of the error correction level used in the barcode, if applicable
   */
  public String getErrorCorrectionLevel() {
    return errorCorrectionLevel;
  }
  
  @Override
  public String toString() {
    StringBuilder dialogText = new StringBuilder(100);
    dialogText.append("Format: ").append(formatName).append('\n');
    dialogText.append("Contents: ").append(contents).append('\n');
    int rawBytesLength = rawBytes == null ? 0 : rawBytes.length;
    dialogText.append("Raw bytes: (").append(rawBytesLength).append(" bytes)\n");
    dialogText.append("Orientation: ").append(orientation).append('\n');
    dialogText.append("EC level: ").append(errorCorrectionLevel).append('\n');
    return dialogText.toString();
  }

}

