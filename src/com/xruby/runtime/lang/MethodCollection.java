/** 
 * Copyright 2005-2007 Xue Yong Zhi
 * Distributed under the GNU General Public License 2.0
 */

package com.xruby.runtime.lang;

import java.util.*;
import com.xruby.runtime.value.*;

abstract class MethodCollection extends ConstantCollection {
	private Map<String, RubyMethod> methods_ = new HashMap<String, RubyMethod>();
	private int current_access_mode_ = RubyMethod.PUBLIC;
	
	MethodCollection(RubyClass c) {
		super(c);
	}
	
	protected void copyMethods(MethodCollection other) {
		methods_.putAll(other.methods_);
	}

	protected RubyMethod findOwnMethod(String method_name) {
		return methods_.get(method_name);
	}

	protected RubyMethod findOwnPublicMethod(String method_name) {
		RubyMethod m = methods_.get(method_name);
		if (null != m && m.isPublic()) {
			return m;
		}

		return null;
	}

	void collectOwnMethodNames(RubyArray a) {
		for (String s : methods_.keySet()) {
			a.add(ObjectFactory.createString(s));
		}
	}

	protected RubyValue addMethod(String name, RubyMethod m) {
		m.setAccess(current_access_mode_);
		methods_.put(name, m);
		return ObjectFactory.nilValue;
	}
	
	public void undefMethod(String name) {
		if (findOwnMethod(name) == null) {
			throw new RubyException(RubyRuntime.NameErrorClass, "undefined method " + name +  " for class `Object'");
		}
		
		methods_.put(name, UndefMethod.getInstance());
	}
	
	public void aliasMethod(String newName, String oldName) {
		RubyMethod m = findOwnMethod(oldName);
		if (null == m) {
			throw new RubyException(RubyRuntime.NameErrorClass, "undefined method " + oldName + " for class `Object'");
		}
		
		methods_.put(newName, m);
	}

	public void setAccessPublic() {
		current_access_mode_ = RubyMethod.PUBLIC;
	}
	
	public void setAccessPrivate() {
		current_access_mode_ = RubyMethod.PRIVATE;
	}
	
	public void setAccessMode(int access) {
		current_access_mode_ = access;
	}

	public RubyMethod setAccess(String method_name, int access) {
		RubyMethod m = findOwnMethod(method_name);
		if (null != m) {
			m.setAccess(access);
		} 

		return m;
	}
}
