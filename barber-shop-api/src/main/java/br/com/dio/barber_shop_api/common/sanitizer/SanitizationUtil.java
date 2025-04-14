package br.com.dio.barber_shop_api.common.sanitizer;

import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;


public class SanitizationUtil {
    // Política que remove todas as tags HTML e retorna apenas texto plano
    private static final PolicyFactory POLICY = new HtmlPolicyBuilder().toFactory();

    public static String sanitize(String input) {
        if (input == null) return null;
        return POLICY.sanitize(input);
    }
}
