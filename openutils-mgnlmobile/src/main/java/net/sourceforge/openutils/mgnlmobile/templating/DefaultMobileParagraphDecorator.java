/**
 *
 * Mobile Module for Magnolia CMS (http://www.openmindlab.com/lab/products/mgnlmobile.html)
 * Copyright(C) 2010-2012, Openmind S.r.l. http://www.openmindonline.it
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

package net.sourceforge.openutils.mgnlmobile.templating;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.openutils.mgnlmobile.filters.MobileFilter;

import org.apache.commons.collections.MapUtils;

/**
 * @author molaschi
 * @version $Id: $
 */
public class DefaultMobileParagraphDecorator extends
		BaseMobileParagraphDecorator {

	Map<String, MobileParagraph> paragraphs = new HashMap<String, MobileParagraph>();

	MobileParagraph defaultParagraph;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Paragraph getMobileParagraph() {
		if (MapUtils.isNotEmpty(paragraphs)) {
			for (MobileParagraph paragraph : paragraphs.values()) {
				if (paragraph.matchDevice(MobileFilter.getDevice())) {
					return paragraph;
				}
			}
		}
		return defaultParagraph;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean hasMobileParagraph() {
		return defaultParagraph != null || MapUtils.isNotEmpty(paragraphs);
	}

	/**
	 * Returns the defaultParagraph.
	 * 
	 * @return the defaultParagraph
	 */
	public MobileParagraph getDefaultParagraph() {
		return defaultParagraph;
	}

	/**
	 * Sets the defaultParagraph.
	 * 
	 * @param defaultParagraph
	 *            the defaultParagraph to set
	 */
	public void setDefaultParagraph(MobileParagraph defaultParagraph) {
		this.defaultParagraph = defaultParagraph;
	}

	/**
	 * Returns the paragraphs.
	 * 
	 * @return the paragraphs
	 */
	public Map<String, MobileParagraph> getParagraphs() {
		return paragraphs;
	}

	/**
	 * Sets the paragraphs.
	 * 
	 * @param paragrahs
	 *            the paragraphs to set
	 */
	public void setParagraphs(Map<String, MobileParagraph> paragraphs) {
		this.paragraphs = paragraphs;
	}

	/**
	 * Sets the templates.
	 * 
	 * @param templates
	 *            the templates to set
	 */
	public void addParagraph(String name, MobileParagraph paragraph) {
		this.paragraphs.put(name, paragraph);
	}

}
