package com.penpencil.loginsignupkmp.data.dto;

import com.penpencil.live_class.presentation.utils.Constants;
import com.penpencil.network.utils.UnzipResult;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.serialization.KSerializer;
import kotlinx.serialization.descriptors.SerialDescriptor;
import kotlinx.serialization.encoding.Decoder;
import kotlinx.serialization.encoding.Encoder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(d1 = {"\u0000:\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\u000e\n\u0002\b\u000f\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0016\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b#\b\u0087\b\u0018\u0000 S2\u00020\u0001:\u0002TUB\u008f\u0001\u0012\b\u0010\u0003\u001a\u0004\u0018\u00010\u0002\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0002\u0012\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u0002\u0012\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u0002\u0012\u0006\u0010\u0007\u001a\u00020\u0002\u0012\u0006\u0010\b\u001a\u00020\u0002\u0012\u0006\u0010\t\u001a\u00020\u0002\u0012\u0006\u0010\n\u001a\u00020\u0002\u0012\n\b\u0002\u0010\u000b\u001a\u0004\u0018\u00010\u0002\u0012\n\b\u0002\u0010\f\u001a\u0004\u0018\u00010\u0002\u0012\n\b\u0002\u0010\r\u001a\u0004\u0018\u00010\u0002\u0012\n\b\u0002\u0010\u000e\u001a\u0004\u0018\u00010\u0002\u0012\n\b\u0002\u0010\u000f\u001a\u0004\u0018\u00010\u0002¢\u0006\u0004\b\u0010\u0010\u0011B\u009d\u0001\b\u0010\u0012\u0006\u0010\u0013\u001a\u00020\u0012\u0012\b\u0010\u0003\u001a\u0004\u0018\u00010\u0002\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0002\u0012\b\u0010\u0005\u001a\u0004\u0018\u00010\u0002\u0012\b\u0010\u0006\u001a\u0004\u0018\u00010\u0002\u0012\b\u0010\u0007\u001a\u0004\u0018\u00010\u0002\u0012\b\u0010\b\u001a\u0004\u0018\u00010\u0002\u0012\b\u0010\t\u001a\u0004\u0018\u00010\u0002\u0012\b\u0010\n\u001a\u0004\u0018\u00010\u0002\u0012\b\u0010\u000b\u001a\u0004\u0018\u00010\u0002\u0012\b\u0010\f\u001a\u0004\u0018\u00010\u0002\u0012\b\u0010\r\u001a\u0004\u0018\u00010\u0002\u0012\b\u0010\u000e\u001a\u0004\u0018\u00010\u0002\u0012\b\u0010\u000f\u001a\u0004\u0018\u00010\u0002\u0012\b\u0010\u0015\u001a\u0004\u0018\u00010\u0014¢\u0006\u0004\b\u0010\u0010\u0016J\u0012\u0010\u0017\u001a\u0004\u0018\u00010\u0002HÆ\u0003¢\u0006\u0004\b\u0017\u0010\u0018J\u0012\u0010\u0019\u001a\u0004\u0018\u00010\u0002HÆ\u0003¢\u0006\u0004\b\u0019\u0010\u0018J\u0012\u0010\u001a\u001a\u0004\u0018\u00010\u0002HÆ\u0003¢\u0006\u0004\b\u001a\u0010\u0018J\u0012\u0010\u001b\u001a\u0004\u0018\u00010\u0002HÆ\u0003¢\u0006\u0004\b\u001b\u0010\u0018J\u0010\u0010\u001c\u001a\u00020\u0002HÆ\u0003¢\u0006\u0004\b\u001c\u0010\u0018J\u0010\u0010\u001d\u001a\u00020\u0002HÆ\u0003¢\u0006\u0004\b\u001d\u0010\u0018J\u0010\u0010\u001e\u001a\u00020\u0002HÆ\u0003¢\u0006\u0004\b\u001e\u0010\u0018J\u0010\u0010\u001f\u001a\u00020\u0002HÆ\u0003¢\u0006\u0004\b\u001f\u0010\u0018J\u0012\u0010 \u001a\u0004\u0018\u00010\u0002HÆ\u0003¢\u0006\u0004\b \u0010\u0018J\u0012\u0010!\u001a\u0004\u0018\u00010\u0002HÆ\u0003¢\u0006\u0004\b!\u0010\u0018J\u0012\u0010\"\u001a\u0004\u0018\u00010\u0002HÆ\u0003¢\u0006\u0004\b\"\u0010\u0018J\u0012\u0010#\u001a\u0004\u0018\u00010\u0002HÆ\u0003¢\u0006\u0004\b#\u0010\u0018J\u0012\u0010$\u001a\u0004\u0018\u00010\u0002HÆ\u0003¢\u0006\u0004\b$\u0010\u0018J¤\u0001\u0010%\u001a\u00020\u00002\n\b\u0002\u0010\u0003\u001a\u0004\u0018\u00010\u00022\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u00022\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u00022\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u00022\b\b\u0002\u0010\u0007\u001a\u00020\u00022\b\b\u0002\u0010\b\u001a\u00020\u00022\b\b\u0002\u0010\t\u001a\u00020\u00022\b\b\u0002\u0010\n\u001a\u00020\u00022\n\b\u0002\u0010\u000b\u001a\u0004\u0018\u00010\u00022\n\b\u0002\u0010\f\u001a\u0004\u0018\u00010\u00022\n\b\u0002\u0010\r\u001a\u0004\u0018\u00010\u00022\n\b\u0002\u0010\u000e\u001a\u0004\u0018\u00010\u00022\n\b\u0002\u0010\u000f\u001a\u0004\u0018\u00010\u0002HÆ\u0001¢\u0006\u0004\b%\u0010&J\u0010\u0010'\u001a\u00020\u0002HÖ\u0001¢\u0006\u0004\b'\u0010\u0018J\u0010\u0010(\u001a\u00020\u0012HÖ\u0001¢\u0006\u0004\b(\u0010)J\u001a\u0010,\u001a\u00020+2\b\u0010*\u001a\u0004\u0018\u00010\u0001HÖ\u0003¢\u0006\u0004\b,\u0010-J'\u00106\u001a\u0002032\u0006\u0010.\u001a\u00020\u00002\u0006\u00100\u001a\u00020/2\u0006\u00102\u001a\u000201H\u0001¢\u0006\u0004\b4\u00105R\"\u0010\u0003\u001a\u0004\u0018\u00010\u00028\u0006X\u0087\u0004¢\u0006\u0012\n\u0004\b\u0003\u00107\u0012\u0004\b9\u0010:\u001a\u0004\b8\u0010\u0018R\"\u0010\u0004\u001a\u0004\u0018\u00010\u00028\u0006X\u0087\u0004¢\u0006\u0012\n\u0004\b\u0004\u00107\u0012\u0004\b<\u0010:\u001a\u0004\b;\u0010\u0018R\"\u0010\u0005\u001a\u0004\u0018\u00010\u00028\u0006X\u0087\u0004¢\u0006\u0012\n\u0004\b\u0005\u00107\u0012\u0004\b>\u0010:\u001a\u0004\b=\u0010\u0018R\"\u0010\u0006\u001a\u0004\u0018\u00010\u00028\u0006X\u0087\u0004¢\u0006\u0012\n\u0004\b\u0006\u00107\u0012\u0004\b@\u0010:\u001a\u0004\b?\u0010\u0018R \u0010\u0007\u001a\u00020\u00028\u0006X\u0087\u0004¢\u0006\u0012\n\u0004\b\u0007\u00107\u0012\u0004\bB\u0010:\u001a\u0004\bA\u0010\u0018R \u0010\b\u001a\u00020\u00028\u0006X\u0087\u0004¢\u0006\u0012\n\u0004\b\b\u00107\u0012\u0004\bD\u0010:\u001a\u0004\bC\u0010\u0018R \u0010\t\u001a\u00020\u00028\u0006X\u0087\u0004¢\u0006\u0012\n\u0004\b\t\u00107\u0012\u0004\bF\u0010:\u001a\u0004\bE\u0010\u0018R \u0010\n\u001a\u00020\u00028\u0006X\u0087\u0004¢\u0006\u0012\n\u0004\b\n\u00107\u0012\u0004\bH\u0010:\u001a\u0004\bG\u0010\u0018R\"\u0010\u000b\u001a\u0004\u0018\u00010\u00028\u0006X\u0087\u0004¢\u0006\u0012\n\u0004\b\u000b\u00107\u0012\u0004\bJ\u0010:\u001a\u0004\bI\u0010\u0018R\"\u0010\f\u001a\u0004\u0018\u00010\u00028\u0006X\u0087\u0004¢\u0006\u0012\n\u0004\b\f\u00107\u0012\u0004\bL\u0010:\u001a\u0004\bK\u0010\u0018R\"\u0010\r\u001a\u0004\u0018\u00010\u00028\u0006X\u0087\u0004¢\u0006\u0012\n\u0004\b\r\u00107\u0012\u0004\bN\u0010:\u001a\u0004\bM\u0010\u0018R\"\u0010\u000e\u001a\u0004\u0018\u00010\u00028\u0006X\u0087\u0004¢\u0006\u0012\n\u0004\b\u000e\u00107\u0012\u0004\bP\u0010:\u001a\u0004\bO\u0010\u0018R\"\u0010\u000f\u001a\u0004\u0018\u00010\u00028\u0006X\u0087\u0004¢\u0006\u0012\n\u0004\b\u000f\u00107\u0012\u0004\bR\u0010:\u001a\u0004\bQ\u0010\u0018¨\u0006V"}, d2 = {"Lcom/penpencil/loginsignupkmp/data/dto/LoginBodyDto;", "", "", "username", "otp", "referralCode", "linkToken", "clientId", "clientSecret", "grantType", "organizationId", "userId", "deviceFingerPrint", "publicKey", "signature", "clickId", "<init>", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V", "", "seen0", "Lq741;", "serializationConstructorMarker", "(ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lq741;)V", "component1", "()Ljava/lang/String;", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "component10", "component11", "component12", "component13", "copy", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Lcom/penpencil/loginsignupkmp/data/dto/LoginBodyDto;", "toString", "hashCode", "()I", "other", "", "equals", "(Ljava/lang/Object;)Z", "self", "Lh6o;", "output", "Lkotlinx/serialization/descriptors/SerialDescriptor;", "serialDesc", "", "write$Self$login_signup_kmp_pwProdRelease", "(Lcom/penpencil/loginsignupkmp/data/dto/LoginBodyDto;Lh6o;Lkotlinx/serialization/descriptors/SerialDescriptor;)V", "write$Self", "Ljava/lang/String;", "getUsername", "getUsername$annotations", "()V", "getOtp", "getOtp$annotations", "getReferralCode", "getReferralCode$annotations", "getLinkToken", "getLinkToken$annotations", "getClientId", "getClientId$annotations", "getClientSecret", "getClientSecret$annotations", "getGrantType", "getGrantType$annotations", "getOrganizationId", "getOrganizationId$annotations", "getUserId", "getUserId$annotations", "getDeviceFingerPrint", "getDeviceFingerPrint$annotations", "getPublicKey", "getPublicKey$annotations", "getSignature", "getSignature$annotations", "getClickId", "getClickId$annotations", "Companion", "a", "b", "login-signup-kmp_pwProdRelease"}, k = 1, mv = {2, UnzipResult.Failure.FAILED_TO_PREPARE_OUTPUT_DIRECTORY, Constants.$stable}, xi = 48)
@l741
/* loaded from: /tmp/opencode/pwdex/classes18.dex */
public final /* data */ class LoginBodyDto {
    public static final int $stable = 0;

    /* renamed from: Companion, reason: from kotlin metadata */
    @NotNull
    public static final Companion INSTANCE = new Companion();

    @Nullable
    private final String clickId;

    @NotNull
    private final String clientId;

    @NotNull
    private final String clientSecret;

    @Nullable
    private final String deviceFingerPrint;

    @NotNull
    private final String grantType;

    @Nullable
    private final String linkToken;

    @NotNull
    private final String organizationId;

    @Nullable
    private final String otp;

    @Nullable
    private final String publicKey;

    @Nullable
    private final String referralCode;

    @Nullable
    private final String signature;

    @Nullable
    private final String userId;

    @Nullable
    private final String username;

    @aos
    public static final /* synthetic */ class a implements gy10<LoginBodyDto> {

        @NotNull
        public static final a a;

        @NotNull
        private static final SerialDescriptor descriptor;

        static {
            a aVar = new a();
            a = aVar;
            s6s0 s6s0Var = new s6s0("com.penpencil.loginsignupkmp.data.dto.LoginBodyDto", aVar, 13);
            s6s0Var.k("username", false);
            s6s0Var.k("otp", false);
            s6s0Var.k("referralCode", true);
            s6s0Var.k("linkToken", true);
            s6s0Var.k("client_id", false);
            s6s0Var.k("client_secret", false);
            s6s0Var.k("grant_type", false);
            s6s0Var.k("organizationId", false);
            s6s0Var.k("userId", true);
            s6s0Var.k("deviceFingerprint", true);
            s6s0Var.k("publicKey", true);
            s6s0Var.k("signature", true);
            s6s0Var.k("clickId", true);
            descriptor = s6s0Var;
        }

        @NotNull
        public final KSerializer<?>[] childSerializers() {
            KSerializer<?> kSerializer = mz81.a;
            return new KSerializer[]{gic.c(kSerializer), gic.c(kSerializer), gic.c(kSerializer), gic.c(kSerializer), kSerializer, kSerializer, kSerializer, kSerializer, gic.c(kSerializer), gic.c(kSerializer), gic.c(kSerializer), gic.c(kSerializer), gic.c(kSerializer)};
        }

        public final Object deserialize(Decoder decoder) {
            String str;
            Intrinsics.checkNotNullParameter(decoder, "decoder");
            SerialDescriptor serialDescriptor = descriptor;
            f6o c = decoder.c(serialDescriptor);
            String str2 = null;
            String str3 = null;
            String str4 = null;
            String str5 = null;
            String str6 = null;
            String str7 = null;
            String str8 = null;
            String str9 = null;
            String str10 = null;
            String str11 = null;
            String str12 = null;
            String str13 = null;
            String str14 = null;
            int i = 0;
            boolean z = true;
            while (true) {
                String str15 = str10;
                if (!z) {
                    c.d(serialDescriptor);
                    return new LoginBodyDto(i, str6, str7, str8, str9, str10, str11, str12, str13, str14, str2, str4, str5, str3, (q741) null);
                }
                int w = c.w(serialDescriptor);
                switch (w) {
                    case Constants.UID_NOT_SET /* -1 */:
                        str = str11;
                        z = false;
                        str10 = str15;
                        str11 = str;
                    case Constants.$stable /* 0 */:
                        str = str11;
                        str6 = (String) c.t(serialDescriptor, 0, mz81.a, str6);
                        i |= 1;
                        str10 = str15;
                        str11 = str;
                    case 1:
                        str = str11;
                        str7 = (String) c.t(serialDescriptor, 1, mz81.a, str7);
                        i |= 2;
                        str10 = str15;
                        str11 = str;
                    case 2:
                        str = str11;
                        str8 = (String) c.t(serialDescriptor, 2, mz81.a, str8);
                        i |= 4;
                        str10 = str15;
                        str11 = str;
                    case UnzipResult.Failure.FAILED_TO_PREPARE_OUTPUT_DIRECTORY /* 3 */:
                        str = str11;
                        str9 = (String) c.t(serialDescriptor, 3, mz81.a, str9);
                        i |= 8;
                        str10 = str15;
                        str11 = str;
                    case UnzipResult.Failure.UNZIP_FAILED /* 4 */:
                        str10 = c.k(serialDescriptor, 4);
                        i |= 16;
                    case UnzipResult.Failure.UNZIP_FAILED_DUE_TO_FILE_MISSING /* 5 */:
                        str11 = c.k(serialDescriptor, 5);
                        i |= 32;
                        str10 = str15;
                    case 6:
                        str12 = c.k(serialDescriptor, 6);
                        i |= 64;
                        str10 = str15;
                    case 7:
                        str13 = c.k(serialDescriptor, 7);
                        i |= 128;
                        str10 = str15;
                    case 8:
                        str = str11;
                        str14 = (String) c.t(serialDescriptor, 8, mz81.a, str14);
                        i |= 256;
                        str10 = str15;
                        str11 = str;
                    case 9:
                        str = str11;
                        str2 = (String) c.t(serialDescriptor, 9, mz81.a, str2);
                        i |= 512;
                        str10 = str15;
                        str11 = str;
                    case Constants.DEFAULT_MAX_UNMUTED_ONSTAGE_USERS /* 10 */:
                        str = str11;
                        str4 = (String) c.t(serialDescriptor, 10, mz81.a, str4);
                        i |= 1024;
                        str10 = str15;
                        str11 = str;
                    case 11:
                        str = str11;
                        str5 = (String) c.t(serialDescriptor, 11, mz81.a, str5);
                        i |= 2048;
                        str10 = str15;
                        str11 = str;
                    case 12:
                        str = str11;
                        str3 = (String) c.t(serialDescriptor, 12, mz81.a, str3);
                        i |= 4096;
                        str10 = str15;
                        str11 = str;
                    default:
                        throw new t5h1(w);
                }
            }
        }

        @NotNull
        public final SerialDescriptor getDescriptor() {
            return descriptor;
        }

        public final void serialize(Encoder encoder, Object obj) {
            LoginBodyDto loginBodyDto = (LoginBodyDto) obj;
            Intrinsics.checkNotNullParameter(encoder, "encoder");
            Intrinsics.checkNotNullParameter(loginBodyDto, "value");
            SerialDescriptor serialDescriptor = descriptor;
            h6o c = encoder.c(serialDescriptor);
            LoginBodyDto.write$Self$login_signup_kmp_pwProdRelease(loginBodyDto, c, serialDescriptor);
            c.d(serialDescriptor);
        }

        @NotNull
        public final /* bridge */ KSerializer<?>[] typeParametersSerializers() {
            return t6s0.a;
        }
    }

    /* renamed from: com.penpencil.loginsignupkmp.data.dto.LoginBodyDto$b, reason: from kotlin metadata */
    public static final class Companion {
        @NotNull
        public final KSerializer<LoginBodyDto> serializer() {
            return a.a;
        }
    }

    public /* synthetic */ LoginBodyDto(int i, String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, String str13, q741 q741Var) {
        if (243 != (i & 243)) {
            o6s0.b(i, 243, a.a.getDescriptor());
            throw null;
        }
        this.username = str;
        this.otp = str2;
        if ((i & 4) == 0) {
            this.referralCode = null;
        } else {
            this.referralCode = str3;
        }
        if ((i & 8) == 0) {
            this.linkToken = null;
        } else {
            this.linkToken = str4;
        }
        this.clientId = str5;
        this.clientSecret = str6;
        this.grantType = str7;
        this.organizationId = str8;
        if ((i & 256) == 0) {
            this.userId = null;
        } else {
            this.userId = str9;
        }
        if ((i & 512) == 0) {
            this.deviceFingerPrint = null;
        } else {
            this.deviceFingerPrint = str10;
        }
        if ((i & 1024) == 0) {
            this.publicKey = null;
        } else {
            this.publicKey = str11;
        }
        if ((i & 2048) == 0) {
            this.signature = null;
        } else {
            this.signature = str12;
        }
        if ((i & 4096) == 0) {
            this.clickId = null;
        } else {
            this.clickId = str13;
        }
    }

    public static /* synthetic */ LoginBodyDto copy$default(LoginBodyDto loginBodyDto, String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, String str13, int i, Object obj) {
        if ((i & 1) != 0) {
            str = loginBodyDto.username;
        }
        return loginBodyDto.copy(str, (i & 2) != 0 ? loginBodyDto.otp : str2, (i & 4) != 0 ? loginBodyDto.referralCode : str3, (i & 8) != 0 ? loginBodyDto.linkToken : str4, (i & 16) != 0 ? loginBodyDto.clientId : str5, (i & 32) != 0 ? loginBodyDto.clientSecret : str6, (i & 64) != 0 ? loginBodyDto.grantType : str7, (i & 128) != 0 ? loginBodyDto.organizationId : str8, (i & 256) != 0 ? loginBodyDto.userId : str9, (i & 512) != 0 ? loginBodyDto.deviceFingerPrint : str10, (i & 1024) != 0 ? loginBodyDto.publicKey : str11, (i & 2048) != 0 ? loginBodyDto.signature : str12, (i & 4096) != 0 ? loginBodyDto.clickId : str13);
    }

    public static /* synthetic */ void getClickId$annotations() {
    }

    public static /* synthetic */ void getClientId$annotations() {
    }

    public static /* synthetic */ void getClientSecret$annotations() {
    }

    public static /* synthetic */ void getDeviceFingerPrint$annotations() {
    }

    public static /* synthetic */ void getGrantType$annotations() {
    }

    public static /* synthetic */ void getLinkToken$annotations() {
    }

    public static /* synthetic */ void getOrganizationId$annotations() {
    }

    public static /* synthetic */ void getOtp$annotations() {
    }

    public static /* synthetic */ void getPublicKey$annotations() {
    }

    public static /* synthetic */ void getReferralCode$annotations() {
    }

    public static /* synthetic */ void getSignature$annotations() {
    }

    public static /* synthetic */ void getUserId$annotations() {
    }

    public static /* synthetic */ void getUsername$annotations() {
    }

    public static final /* synthetic */ void write$Self$login_signup_kmp_pwProdRelease(LoginBodyDto self, h6o output, SerialDescriptor serialDesc) {
        mz81 mz81Var = mz81.a;
        output.i(serialDesc, 0, mz81Var, self.username);
        output.i(serialDesc, 1, mz81Var, self.otp);
        if (output.q(serialDesc, 2) || self.referralCode != null) {
            output.i(serialDesc, 2, mz81Var, self.referralCode);
        }
        if (output.q(serialDesc, 3) || self.linkToken != null) {
            output.i(serialDesc, 3, mz81Var, self.linkToken);
        }
        output.o(serialDesc, 4, self.clientId);
        output.o(serialDesc, 5, self.clientSecret);
        output.o(serialDesc, 6, self.grantType);
        output.o(serialDesc, 7, self.organizationId);
        if (output.q(serialDesc, 8) || self.userId != null) {
            output.i(serialDesc, 8, mz81Var, self.userId);
        }
        if (output.q(serialDesc, 9) || self.deviceFingerPrint != null) {
            output.i(serialDesc, 9, mz81Var, self.deviceFingerPrint);
        }
        if (output.q(serialDesc, 10) || self.publicKey != null) {
            output.i(serialDesc, 10, mz81Var, self.publicKey);
        }
        if (output.q(serialDesc, 11) || self.signature != null) {
            output.i(serialDesc, 11, mz81Var, self.signature);
        }
        if (!output.q(serialDesc, 12) && self.clickId == null) {
            return;
        }
        output.i(serialDesc, 12, mz81Var, self.clickId);
    }

    @Nullable
    /* renamed from: component1, reason: from getter */
    public final String getUsername() {
        return this.username;
    }

    @Nullable
    /* renamed from: component10, reason: from getter */
    public final String getDeviceFingerPrint() {
        return this.deviceFingerPrint;
    }

    @Nullable
    /* renamed from: component11, reason: from getter */
    public final String getPublicKey() {
        return this.publicKey;
    }

    @Nullable
    /* renamed from: component12, reason: from getter */
    public final String getSignature() {
        return this.signature;
    }

    @Nullable
    /* renamed from: component13, reason: from getter */
    public final String getClickId() {
        return this.clickId;
    }

    @Nullable
    /* renamed from: component2, reason: from getter */
    public final String getOtp() {
        return this.otp;
    }

    @Nullable
    /* renamed from: component3, reason: from getter */
    public final String getReferralCode() {
        return this.referralCode;
    }

    @Nullable
    /* renamed from: component4, reason: from getter */
    public final String getLinkToken() {
        return this.linkToken;
    }

    @NotNull
    /* renamed from: component5, reason: from getter */
    public final String getClientId() {
        return this.clientId;
    }

    @NotNull
    /* renamed from: component6, reason: from getter */
    public final String getClientSecret() {
        return this.clientSecret;
    }

    @NotNull
    /* renamed from: component7, reason: from getter */
    public final String getGrantType() {
        return this.grantType;
    }

    @NotNull
    /* renamed from: component8, reason: from getter */
    public final String getOrganizationId() {
        return this.organizationId;
    }

    @Nullable
    /* renamed from: component9, reason: from getter */
    public final String getUserId() {
        return this.userId;
    }

    @NotNull
    public final LoginBodyDto copy(@Nullable String username, @Nullable String otp, @Nullable String referralCode, @Nullable String linkToken, @NotNull String clientId, @NotNull String clientSecret, @NotNull String grantType, @NotNull String organizationId, @Nullable String userId, @Nullable String deviceFingerPrint, @Nullable String publicKey, @Nullable String signature, @Nullable String clickId) {
        Intrinsics.checkNotNullParameter(clientId, "clientId");
        Intrinsics.checkNotNullParameter(clientSecret, "clientSecret");
        Intrinsics.checkNotNullParameter(grantType, "grantType");
        Intrinsics.checkNotNullParameter(organizationId, "organizationId");
        return new LoginBodyDto(username, otp, referralCode, linkToken, clientId, clientSecret, grantType, organizationId, userId, deviceFingerPrint, publicKey, signature, clickId);
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof LoginBodyDto)) {
            return false;
        }
        LoginBodyDto loginBodyDto = (LoginBodyDto) other;
        return Intrinsics.f(this.username, loginBodyDto.username) && Intrinsics.f(this.otp, loginBodyDto.otp) && Intrinsics.f(this.referralCode, loginBodyDto.referralCode) && Intrinsics.f(this.linkToken, loginBodyDto.linkToken) && Intrinsics.f(this.clientId, loginBodyDto.clientId) && Intrinsics.f(this.clientSecret, loginBodyDto.clientSecret) && Intrinsics.f(this.grantType, loginBodyDto.grantType) && Intrinsics.f(this.organizationId, loginBodyDto.organizationId) && Intrinsics.f(this.userId, loginBodyDto.userId) && Intrinsics.f(this.deviceFingerPrint, loginBodyDto.deviceFingerPrint) && Intrinsics.f(this.publicKey, loginBodyDto.publicKey) && Intrinsics.f(this.signature, loginBodyDto.signature) && Intrinsics.f(this.clickId, loginBodyDto.clickId);
    }

    @Nullable
    public final String getClickId() {
        return this.clickId;
    }

    @NotNull
    public final String getClientId() {
        return this.clientId;
    }

    @NotNull
    public final String getClientSecret() {
        return this.clientSecret;
    }

    @Nullable
    public final String getDeviceFingerPrint() {
        return this.deviceFingerPrint;
    }

    @NotNull
    public final String getGrantType() {
        return this.grantType;
    }

    @Nullable
    public final String getLinkToken() {
        return this.linkToken;
    }

    @NotNull
    public final String getOrganizationId() {
        return this.organizationId;
    }

    @Nullable
    public final String getOtp() {
        return this.otp;
    }

    @Nullable
    public final String getPublicKey() {
        return this.publicKey;
    }

    @Nullable
    public final String getReferralCode() {
        return this.referralCode;
    }

    @Nullable
    public final String getSignature() {
        return this.signature;
    }

    @Nullable
    public final String getUserId() {
        return this.userId;
    }

    @Nullable
    public final String getUsername() {
        return this.username;
    }

    public int hashCode() {
        String str = this.username;
        int hashCode = (str == null ? 0 : str.hashCode()) * 31;
        String str2 = this.otp;
        int hashCode2 = (hashCode + (str2 == null ? 0 : str2.hashCode())) * 31;
        String str3 = this.referralCode;
        int hashCode3 = (hashCode2 + (str3 == null ? 0 : str3.hashCode())) * 31;
        String str4 = this.linkToken;
        int a2 = ms00.a(ms00.a(ms00.a(ms00.a((hashCode3 + (str4 == null ? 0 : str4.hashCode())) * 31, 31, this.clientId), 31, this.clientSecret), 31, this.grantType), 31, this.organizationId);
        String str5 = this.userId;
        int hashCode4 = (a2 + (str5 == null ? 0 : str5.hashCode())) * 31;
        String str6 = this.deviceFingerPrint;
        int hashCode5 = (hashCode4 + (str6 == null ? 0 : str6.hashCode())) * 31;
        String str7 = this.publicKey;
        int hashCode6 = (hashCode5 + (str7 == null ? 0 : str7.hashCode())) * 31;
        String str8 = this.signature;
        int hashCode7 = (hashCode6 + (str8 == null ? 0 : str8.hashCode())) * 31;
        String str9 = this.clickId;
        return hashCode7 + (str9 != null ? str9.hashCode() : 0);
    }

    @NotNull
    public String toString() {
        String str = this.username;
        String str2 = this.otp;
        String str3 = this.referralCode;
        String str4 = this.linkToken;
        String str5 = this.clientId;
        String str6 = this.clientSecret;
        String str7 = this.grantType;
        String str8 = this.organizationId;
        String str9 = this.userId;
        String str10 = this.deviceFingerPrint;
        String str11 = this.publicKey;
        String str12 = this.signature;
        String str13 = this.clickId;
        StringBuilder a2 = ls00.a("LoginBodyDto(username=", str, ", otp=", str2, ", referralCode=");
        h4g1.a(a2, str3, ", linkToken=", str4, ", clientId=");
        h4g1.a(a2, str5, ", clientSecret=", str6, ", grantType=");
        h4g1.a(a2, str7, ", organizationId=", str8, ", userId=");
        h4g1.a(a2, str9, ", deviceFingerPrint=", str10, ", publicKey=");
        h4g1.a(a2, str11, ", signature=", str12, ", clickId=");
        return qt00.a(a2, str13, ")");
    }

    public LoginBodyDto(@Nullable String str, @Nullable String str2, @Nullable String str3, @Nullable String str4, @NotNull String str5, @NotNull String str6, @NotNull String str7, @NotNull String str8, @Nullable String str9, @Nullable String str10, @Nullable String str11, @Nullable String str12, @Nullable String str13) {
        Intrinsics.checkNotNullParameter(str5, "clientId");
        Intrinsics.checkNotNullParameter(str6, "clientSecret");
        Intrinsics.checkNotNullParameter(str7, "grantType");
        Intrinsics.checkNotNullParameter(str8, "organizationId");
        this.username = str;
        this.otp = str2;
        this.referralCode = str3;
        this.linkToken = str4;
        this.clientId = str5;
        this.clientSecret = str6;
        this.grantType = str7;
        this.organizationId = str8;
        this.userId = str9;
        this.deviceFingerPrint = str10;
        this.publicKey = str11;
        this.signature = str12;
        this.clickId = str13;
    }

    public /* synthetic */ LoginBodyDto(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, String str13, int i, DefaultConstructorMarker defaultConstructorMarker) {
        this(str, str2, (i & 4) != 0 ? null : str3, (i & 8) != 0 ? null : str4, str5, str6, str7, str8, (i & 256) != 0 ? null : str9, (i & 512) != 0 ? null : str10, (i & 1024) != 0 ? null : str11, (i & 2048) != 0 ? null : str12, (i & 4096) != 0 ? null : str13);
    }
}
