package com.hadj4r.serializer.factory;

import com.hadj4r.serializer.model.Foo;
import com.hadj4r.serializer.model.WrapperClass;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class SerializersTest {
    @Test
    void shouldReturnImplementationInstance() {
        final Foo serializer = Serializers.getSerializer(Foo.class);
        assertThat(serializer).isNotNull();
    }


    @Test
    void findsNestedSerializerImpl() {
        assertThat(Serializers.getSerializer(WrapperClass.NestedInterface.class)).isNotNull();
        assertThat(Serializers.getSerializer(WrapperClass.NestedClass.NestedNestedInterface.class)).isNotNull();
    }


    @Test
    void shouldReturnPackagePrivateImplementationInstance() {
        assertThat(Serializers.getSerializer(PackagePrivateSerializer.class)).isNotNull();
    }

}
