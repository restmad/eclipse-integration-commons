/*******************************************************************************
 * Copyright (c) 2012 VMWare, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     VMWare, Inc. - initial API and implementation
 *******************************************************************************/
package org.springsource.ide.eclipse.commons.livexp.core;

import org.eclipse.core.runtime.IStatus;

/**
 * A value representing the result of a wizard page validation. 
 * 
 * @author Kris De Volder
 */
public class ValidationResult {
	
	public final String msg;
	public int status; 
	
	private ValidationResult(int status, String msg) {
		this.msg = msg;
		this.status = status;
	}
	public boolean isOk() {
		return status == IStatus.OK;
	}
	public static final ValidationResult OK = new ValidationResult(IStatus.OK, null);
	
	public static ValidationResult error(String msg) {
		return new ValidationResult(IStatus.ERROR, msg);
	}
	
	public static ValidationResult warning(String msg) {
		return new ValidationResult(IStatus.WARNING, msg);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		//generated by eclipse
		final int prime = 31;
		int result = 1;
		result = prime * result + ((msg == null) ? 0 : msg.hashCode());
		result = prime * result + status;
		return result;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		//generated by eclipse
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ValidationResult other = (ValidationResult) obj;
		if (msg == null) {
			if (other.msg != null)
				return false;
		} else if (!msg.equals(other.msg))
			return false;
		if (status != other.status)
			return false;
		return true;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ValidationResult [msg=" + msg + ", status=" + status(status) + "]";
	}
	
	private static String status(int s) {
		switch (s) {
		case IStatus.ERROR:
			return "ERROR";
		case IStatus.WARNING:
			return "WARNING";	
		default:
			return ""+s;
		}
	}
	
	
}
