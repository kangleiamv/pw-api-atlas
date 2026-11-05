package com.penpencil.loginsignupkmp.data.dto;

import com.penpencil.live_class.presentation.utils.Constants;
import com.penpencil.network.utils.UnzipResult;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.serialization.KSerializer;
import kotlinx.serialization.descriptors.SerialDescriptor;
import kotlinx.serialization.encoding.Decoder;
import kotlinx.serialization.encoding.Encoder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(d1 = {"\u0000:\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\r\n\u0002\u0010\u000b\n\u0002\b\u000e\b\u0087\b\u0018\u0000 +2\u00020\u0001:\u0002,-B\u001f\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0004\u001a\u00020\u0002\u0012\u0006\u0010\u0005\u001a\u00020\u0002¢\u0006\u0004\b\u0006\u0010\u0007B9\b\u0010\u0012\u0006\u0010\t\u001a\u00020\b\u0012\b\u0010\u0003\u001a\u0004\u0018\u00010\u0002\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0002\u0012\b\u0010\u0005\u001a\u0004\u0018\u00010\u0002\u0012\b\u0010\u000b\u001a\u0004\u0018\u00010\n¢\u0006\u0004\b\u0006\u0010\fJ'\u0010\u0015\u001a\u00020\u00122\u0006\u0010\r\u001a\u00020\u00002\u0006\u0010\u000f\u001a\u00020\u000e2\u0006\u0010\u0011\u001a\u00020\u0010H\u0001¢\u0006\u0004\b\u0013\u0010\u0014J\u0010\u0010\u0016\u001a\u00020\u0002HÆ\u0003¢\u0006\u0004\b\u0016\u0010\u0017J\u0010\u0010\u0018\u001a\u00020\u0002HÆ\u0003¢\u0006\u0004\b\u0018\u0010\u0017J\u0010\u0010\u0019\u001a\u00020\u0002HÆ\u0003¢\u0006\u0004\b\u0019\u0010\u0017J.\u0010\u001a\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00022\b\b\u0002\u0010\u0004\u001a\u00020\u00022\b\b\u0002\u0010\u0005\u001a\u00020\u0002HÆ\u0001¢\u0006\u0004\b\u001a\u0010\u001bJ\u0010\u0010\u001c\u001a\u00020\u0002HÖ\u0001¢\u0006\u0004\b\u001c\u0010\u0017J\u0010\u0010\u001d\u001a\u00020\bHÖ\u0001¢\u0006\u0004\b\u001d\u0010\u001eJ\u001a\u0010!\u001a\u00020 2\b\u0010\u001f\u001a\u0004\u0018\u00010\u0001HÖ\u0003¢\u0006\u0004\b!\u0010\"R \u0010\u0003\u001a\u00020\u00028\u0006X\u0087\u0004¢\u0006\u0012\n\u0004\b\u0003\u0010#\u0012\u0004\b%\u0010&\u001a\u0004\b$\u0010\u0017R \u0010\u0004\u001a\u00020\u00028\u0006X\u0087\u0004¢\u0006\u0012\n\u0004\b\u0004\u0010#\u0012\u0004\b(\u0010&\u001a\u0004\b'\u0010\u0017R \u0010\u0005\u001a\u00020\u00028\u0006X\u0087\u0004¢\u0006\u0012\n\u0004\b\u0005\u0010#\u0012\u0004\b*\u0010&\u001a\u0004\b)\u0010\u0017¨\u0006."}, d2 = {"Lcom/penpencil/loginsignupkmp/data/dto/OtpPayloadDto;", "", "", "username", "countryCode", "organizationId", "<init>", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V", "", "seen0", "Lq741;", "serializationConstructorMarker", "(ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;Lq741;)V", "self", "Lh6o;", "output", "Lkotlinx/serialization/descriptors/SerialDescriptor;", "serialDesc", "", "write$Self$login_signup_kmp_pwProdRelease", "(Lcom/penpencil/loginsignupkmp/data/dto/OtpPayloadDto;Lh6o;Lkotlinx/serialization/descriptors/SerialDescriptor;)V", "write$Self", "component1", "()Ljava/lang/String;", "component2", "component3", "copy", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Lcom/penpencil/loginsignupkmp/data/dto/OtpPayloadDto;", "toString", "hashCode", "()I", "other", "", "equals", "(Ljava/lang/Object;)Z", "Ljava/lang/String;", "getUsername", "getUsername$annotations", "()V", "getCountryCode", "getCountryCode$annotations", "getOrganizationId", "getOrganizationId$annotations", "Companion", "a", "b", "login-signup-kmp_pwProdRelease"}, k = 1, mv = {2, UnzipResult.Failure.FAILED_TO_PREPARE_OUTPUT_DIRECTORY, Constants.$stable}, xi = 48)
@l741
/* loaded from: /tmp/opencode/pwdex/classes18.dex */
public final /* data */ class OtpPayloadDto {
    public static final int $stable = 0;

    /* renamed from: Companion, reason: from kotlin metadata */
    @NotNull
    public static final Companion INSTANCE = new Companion();

    @NotNull
    private final String countryCode;

    @NotNull
    private final String organizationId;

    @NotNull
    private final String username;

    @aos
    public static final /* synthetic */ class a implements gy10<OtpPayloadDto> {

        @NotNull
        public static final a a;

        @NotNull
        private static final SerialDescriptor descriptor;

        static {
            a aVar = new a();
            a = aVar;
            s6s0 s6s0Var = new s6s0("com.penpencil.loginsignupkmp.data.dto.OtpPayloadDto", aVar, 3);
            s6s0Var.k("username", false);
            s6s0Var.k("countryCode", false);
            s6s0Var.k("organizationId", false);
            descriptor = s6s0Var;
        }

        @NotNull
        public final KSerializer<?>[] childSerializers() {
            mz81 mz81Var = mz81.a;
            return new KSerializer[]{mz81Var, mz81Var, mz81Var};
        }

        public final Object deserialize(Decoder decoder) {
            Intrinsics.checkNotNullParameter(decoder, "decoder");
            SerialDescriptor serialDescriptor = descriptor;
            f6o c = decoder.c(serialDescriptor);
            int i = 0;
            String str = null;
            String str2 = null;
            String str3 = null;
            boolean z = true;
            while (z) {
                int w = c.w(serialDescriptor);
                if (w == -1) {
                    z = false;
                } else if (w == 0) {
                    str = c.k(serialDescriptor, 0);
                    i |= 1;
                } else if (w == 1) {
                    str2 = c.k(serialDescriptor, 1);
                    i |= 2;
                } else {
                    if (w != 2) {
                        throw new t5h1(w);
                    }
                    str3 = c.k(serialDescriptor, 2);
                    i |= 4;
                }
            }
            c.d(serialDescriptor);
            return new OtpPayloadDto(i, str, str2, str3, null);
        }

        @NotNull
        public final SerialDescriptor getDescriptor() {
            return descriptor;
        }

        public final void serialize(Encoder encoder, Object obj) {
            OtpPayloadDto otpPayloadDto = (OtpPayloadDto) obj;
            Intrinsics.checkNotNullParameter(encoder, "encoder");
            Intrinsics.checkNotNullParameter(otpPayloadDto, "value");
            SerialDescriptor serialDescriptor = descriptor;
            h6o c = encoder.c(serialDescriptor);
            OtpPayloadDto.write$Self$login_signup_kmp_pwProdRelease(otpPayloadDto, c, serialDescriptor);
            c.d(serialDescriptor);
        }

        @NotNull
        public final /* bridge */ KSerializer<?>[] typeParametersSerializers() {
            return t6s0.a;
        }
    }

    /* renamed from: com.penpencil.loginsignupkmp.data.dto.OtpPayloadDto$b, reason: from kotlin metadata */
    public static final class Companion {
        @NotNull
        public final KSerializer<OtpPayloadDto> serializer() {
            return a.a;
        }
    }

    public /* synthetic */ OtpPayloadDto(int i, String str, String str2, String str3, q741 q741Var) {
        if (7 != (i & 7)) {
            o6s0.b(i, 7, a.a.getDescriptor());
            throw null;
        }
        this.username = str;
        this.countryCode = str2;
        this.organizationId = str3;
    }

    public static /* synthetic */ OtpPayloadDto copy$default(OtpPayloadDto otpPayloadDto, String str, String str2, String str3, int i, Object obj) {
        if ((i & 1) != 0) {
            str = otpPayloadDto.username;
        }
        if ((i & 2) != 0) {
            str2 = otpPayloadDto.countryCode;
        }
        if ((i & 4) != 0) {
            str3 = otpPayloadDto.organizationId;
        }
        return otpPayloadDto.copy(str, str2, str3);
    }

    public static /* synthetic */ void getCountryCode$annotations() {
    }

    public static /* synthetic */ void getOrganizationId$annotations() {
    }

    public static /* synthetic */ void getUsername$annotations() {
    }

    public static final /* synthetic */ void write$Self$login_signup_kmp_pwProdRelease(OtpPayloadDto self, h6o output, SerialDescriptor serialDesc) {
        output.o(serialDesc, 0, self.username);
        output.o(serialDesc, 1, self.countryCode);
        output.o(serialDesc, 2, self.organizationId);
    }

    @NotNull
    /* renamed from: component1, reason: from getter */
    public final String getUsername() {
        return this.username;
    }

    @NotNull
    /* renamed from: component2, reason: from getter */
    public final String getCountryCode() {
        return this.countryCode;
    }

    @NotNull
    /* renamed from: component3, reason: from getter */
    public final String getOrganizationId() {
        return this.organizationId;
    }

    @NotNull
    public final OtpPayloadDto copy(@NotNull String username, @NotNull String countryCode, @NotNull String organizationId) {
        Intrinsics.checkNotNullParameter(username, "username");
        Intrinsics.checkNotNullParameter(countryCode, "countryCode");
        Intrinsics.checkNotNullParameter(organizationId, "organizationId");
        return new OtpPayloadDto(username, countryCode, organizationId);
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof OtpPayloadDto)) {
            return false;
        }
        OtpPayloadDto otpPayloadDto = (OtpPayloadDto) other;
        return Intrinsics.f(this.username, otpPayloadDto.username) && Intrinsics.f(this.countryCode, otpPayloadDto.countryCode) && Intrinsics.f(this.organizationId, otpPayloadDto.organizationId);
    }

    @NotNull
    public final String getCountryCode() {
        return this.countryCode;
    }

    @NotNull
    public final String getOrganizationId() {
        return this.organizationId;
    }

    @NotNull
    public final String getUsername() {
        return this.username;
    }

    public int hashCode() {
        return this.organizationId.hashCode() + ms00.a(this.username.hashCode() * 31, 31, this.countryCode);
    }

    @NotNull
    public String toString() {
        String str = this.username;
        String str2 = this.countryCode;
        return qt00.a(ls00.a("OtpPayloadDto(username=", str, ", countryCode=", str2, ", organizationId="), this.organizationId, ")");
    }

    public OtpPayloadDto(@NotNull String str, @NotNull String str2, @NotNull String str3) {
        Intrinsics.checkNotNullParameter(str, "username");
        Intrinsics.checkNotNullParameter(str2, "countryCode");
        Intrinsics.checkNotNullParameter(str3, "organizationId");
        this.username = str;
        this.countryCode = str2;
        this.organizationId = str3;
    }
}
