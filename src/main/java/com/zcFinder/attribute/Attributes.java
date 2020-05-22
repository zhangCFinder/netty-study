package com.zcFinder.attribute;

import io.netty.util.AttributeKey;
import com.zcFinder.session.Session;

public interface Attributes {
    AttributeKey<Session> SESSION = AttributeKey.newInstance("session");
}
