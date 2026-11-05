package com.penpencil.loginsignupkmp.data.dto;

import com.penpencil.live_class.presentation.utils.Constants;
import com.penpencil.loginsignupkmp.data.dto.PiracyAcknowledgeDataDto;
import com.penpencil.loginsignupkmp.data.dto.UserDto;
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

@Metadata(d1 = {"\u0000N\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u001a\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b!\b\u0087\b\u0018\u0000 R2\u00020\u0001:\u0002STB\u007f\u0012\n\b\u0002\u0010\u0003\u001a\u0004\u0018\u00010\u0002\u0012\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0002\u0012\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u0005\u0012\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\u0002\u0012\n\b\u0002\u0010\t\u001a\u0004\u0018\u00010\b\u0012\n\b\u0002\u0010\u000b\u001a\u0004\u0018\u00010\n\u0012\n\b\u0002\u0010\r\u001a\u0004\u0018\u00010\f\u0012\n\b\u0002\u0010\u000e\u001a\u0004\u0018\u00010\n\u0012\n\b\u0002\u0010\u000f\u001a\u0004\u0018\u00010\u0002\u0012\n\b\u0002\u0010\u0010\u001a\u0004\u0018\u00010\u0002¢\u0006\u0004\b\u0011\u0010\u0012B\u007f\b\u0010\u0012\u0006\u0010\u0014\u001a\u00020\u0013\u0012\b\u0010\u0003\u001a\u0004\u0018\u00010\u0002\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0002\u0012\b\u0010\u0006\u001a\u0004\u0018\u00010\u0005\u0012\b\u0010\u0007\u001a\u0004\u0018\u00010\u0002\u0012\b\u0010\t\u001a\u0004\u0018\u00010\b\u0012\b\u0010\u000b\u001a\u0004\u0018\u00010\n\u0012\b\u0010\r\u001a\u0004\u0018\u00010\f\u0012\b\u0010\u000e\u001a\u0004\u0018\u00010\n\u0012\b\u0010\u000f\u001a\u0004\u0018\u00010\u0002\u0012\b\u0010\u0010\u001a\u0004\u0018\u00010\u0002\u0012\b\u0010\u0016\u001a\u0004\u0018\u00010\u0015¢\u0006\u0004\b\u0011\u0010\u0017J\u0012\u0010\u0018\u001a\u0004\u0018\u00010\u0002HÆ\u0003¢\u0006\u0004\b\u0018\u0010\u0019J\u0012\u0010\u001a\u001a\u0004\u0018\u00010\u0002HÆ\u0003¢\u0006\u0004\b\u001a\u0010\u0019J\u0012\u0010\u001b\u001a\u0004\u0018\u00010\u0005HÆ\u0003¢\u0006\u0004\b\u001b\u0010\u001cJ\u0012\u0010\u001d\u001a\u0004\u0018\u00010\u0002HÆ\u0003¢\u0006\u0004\b\u001d\u0010\u0019J\u0012\u0010\u001e\u001a\u0004\u0018\u00010\bHÆ\u0003¢\u0006\u0004\b\u001e\u0010\u001fJ\u0012\u0010 \u001a\u0004\u0018\u00010\nHÆ\u0003¢\u0006\u0004\b \u0010!J\u0012\u0010\"\u001a\u0004\u0018\u00010\fHÆ\u0003¢\u0006\u0004\b\"\u0010#J\u0012\u0010$\u001a\u0004\u0018\u00010\nHÆ\u0003¢\u0006\u0004\b$\u0010!J\u0012\u0010%\u001a\u0004\u0018\u00010\u0002HÆ\u0003¢\u0006\u0004\b%\u0010\u0019J\u0012\u0010&\u001a\u0004\u0018\u00010\u0002HÆ\u0003¢\u0006\u0004\b&\u0010\u0019J\u0088\u0001\u0010'\u001a\u00020\u00002\n\b\u0002\u0010\u0003\u001a\u0004\u0018\u00010\u00022\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u00022\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u00052\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\u00022\n\b\u0002\u0010\t\u001a\u0004\u0018\u00010\b2\n\b\u0002\u0010\u000b\u001a\u0004\u0018\u00010\n2\n\b\u0002\u0010\r\u001a\u0004\u0018\u00010\f2\n\b\u0002\u0010\u000e\u001a\u0004\u0018\u00010\n2\n\b\u0002\u0010\u000f\u001a\u0004\u0018\u00010\u00022\n\b\u0002\u0010\u0010\u001a\u0004\u0018\u00010\u0002HÆ\u0001¢\u0006\u0004\b'\u0010(J\u0010\u0010)\u001a\u00020\u0002HÖ\u0001¢\u0006\u0004\b)\u0010\u0019J\u0010\u0010*\u001a\u00020\u0013HÖ\u0001¢\u0006\u0004\b*\u0010+J\u001a\u0010-\u001a\u00020\n2\b\u0010,\u001a\u0004\u0018\u00010\u0001HÖ\u0003¢\u0006\u0004\b-\u0010.J'\u00107\u001a\u0002042\u0006\u0010/\u001a\u00020\u00002\u0006\u00101\u001a\u0002002\u0006\u00103\u001a\u000202H\u0001¢\u0006\u0004\b5\u00106R\"\u0010\u0003\u001a\u0004\u0018\u00010\u00028\u0006X\u0087\u0004¢\u0006\u0012\n\u0004\b\u0003\u00108\u0012\u0004\b:\u0010;\u001a\u0004\b9\u0010\u0019R\"\u0010\u0004\u001a\u0004\u0018\u00010\u00028\u0006X\u0087\u0004¢\u0006\u0012\n\u0004\b\u0004\u00108\u0012\u0004\b=\u0010;\u001a\u0004\b<\u0010\u0019R\"\u0010\u0006\u001a\u0004\u0018\u00010\u00058\u0006X\u0087\u0004¢\u0006\u0012\n\u0004\b\u0006\u0010>\u0012\u0004\b@\u0010;\u001a\u0004\b?\u0010\u001cR\"\u0010\u0007\u001a\u0004\u0018\u00010\u00028\u0006X\u0087\u0004¢\u0006\u0012\n\u0004\b\u0007\u00108\u0012\u0004\bB\u0010;\u001a\u0004\bA\u0010\u0019R\"\u0010\t\u001a\u0004\u0018\u00010\b8\u0006X\u0087\u0004¢\u0006\u0012\n\u0004\b\t\u0010C\u0012\u0004\bE\u0010;\u001a\u0004\bD\u0010\u001fR\"\u0010\u000b\u001a\u0004\u0018\u00010\n8\u0006X\u0087\u0004¢\u0006\u0012\n\u0004\b\u000b\u0010F\u0012\u0004\bH\u0010;\u001a\u0004\bG\u0010!R\"\u0010\r\u001a\u0004\u0018\u00010\f8\u0006X\u0087\u0004¢\u0006\u0012\n\u0004\b\r\u0010I\u0012\u0004\bK\u0010;\u001a\u0004\bJ\u0010#R\"\u0010\u000e\u001a\u0004\u0018\u00010\n8\u0006X\u0087\u0004¢\u0006\u0012\n\u0004\b\u000e\u0010F\u0012\u0004\bM\u0010;\u001a\u0004\bL\u0010!R\"\u0010\u000f\u001a\u0004\u0018\u00010\u00028\u0006X\u0087\u0004¢\u0006\u0012\n\u0004\b\u000f\u00108\u0012\u0004\bO\u0010;\u001a\u0004\bN\u0010\u0019R\"\u0010\u0010\u001a\u0004\u0018\u00010\u00028\u0006X\u0087\u0004¢\u0006\u0012\n\u0004\b\u0010\u00108\u0012\u0004\bQ\u0010;\u001a\u0004\bP\u0010\u0019¨\u0006U"}, d2 = {"Lcom/penpencil/loginsignupkmp/data/dto/LoginDataDto;", "", "", "accessToken", "refreshToken", "", "expiresIn", "tokenId", "Lcom/penpencil/loginsignupkmp/data/dto/UserDto;", "user", "", "acknowledgeRequired", "Lcom/penpencil/loginsignupkmp/data/dto/PiracyAcknowledgeDataDto;", "acknowledgeData", "deviceLimitBreached", "deviceToken", "mobileNumber", "<init>", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Long;Ljava/lang/String;Lcom/penpencil/loginsignupkmp/data/dto/UserDto;Ljava/lang/Boolean;Lcom/penpencil/loginsignupkmp/data/dto/PiracyAcknowledgeDataDto;Ljava/lang/Boolean;Ljava/lang/String;Ljava/lang/String;)V", "", "seen0", "Lq741;", "serializationConstructorMarker", "(ILjava/lang/String;Ljava/lang/String;Ljava/lang/Long;Ljava/lang/String;Lcom/penpencil/loginsignupkmp/data/dto/UserDto;Ljava/lang/Boolean;Lcom/penpencil/loginsignupkmp/data/dto/PiracyAcknowledgeDataDto;Ljava/lang/Boolean;Ljava/lang/String;Ljava/lang/String;Lq741;)V", "component1", "()Ljava/lang/String;", "component2", "component3", "()Ljava/lang/Long;", "component4", "component5", "()Lcom/penpencil/loginsignupkmp/data/dto/UserDto;", "component6", "()Ljava/lang/Boolean;", "component7", "()Lcom/penpencil/loginsignupkmp/data/dto/PiracyAcknowledgeDataDto;", "component8", "component9", "component10", "copy", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Long;Ljava/lang/String;Lcom/penpencil/loginsignupkmp/data/dto/UserDto;Ljava/lang/Boolean;Lcom/penpencil/loginsignupkmp/data/dto/PiracyAcknowledgeDataDto;Ljava/lang/Boolean;Ljava/lang/String;Ljava/lang/String;)Lcom/penpencil/loginsignupkmp/data/dto/LoginDataDto;", "toString", "hashCode", "()I", "other", "equals", "(Ljava/lang/Object;)Z", "self", "Lh6o;", "output", "Lkotlinx/serialization/descriptors/SerialDescriptor;", "serialDesc", "", "write$Self$login_signup_kmp_pwProdRelease", "(Lcom/penpencil/loginsignupkmp/data/dto/LoginDataDto;Lh6o;Lkotlinx/serialization/descriptors/SerialDescriptor;)V", "write$Self", "Ljava/lang/String;", "getAccessToken", "getAccessToken$annotations", "()V", "getRefreshToken", "getRefreshToken$annotations", "Ljava/lang/Long;", "getExpiresIn", "getExpiresIn$annotations", "getTokenId", "getTokenId$annotations", "Lcom/penpencil/loginsignupkmp/data/dto/UserDto;", "getUser", "getUser$annotations", "Ljava/lang/Boolean;", "getAcknowledgeRequired", "getAcknowledgeRequired$annotations", "Lcom/penpencil/loginsignupkmp/data/dto/PiracyAcknowledgeDataDto;", "getAcknowledgeData", "getAcknowledgeData$annotations", "getDeviceLimitBreached", "getDeviceLimitBreached$annotations", "getDeviceToken", "getDeviceToken$annotations", "getMobileNumber", "getMobileNumber$annotations", "Companion", "a", "b", "login-signup-kmp_pwProdRelease"}, k = 1, mv = {2, UnzipResult.Failure.FAILED_TO_PREPARE_OUTPUT_DIRECTORY, Constants.$stable}, xi = 48)
@l741
/* loaded from: /tmp/opencode/pwdex/classes18.dex */
public final /* data */ class LoginDataDto {

    @Nullable
    private final String accessToken;

    @Nullable
    private final PiracyAcknowledgeDataDto acknowledgeData;

    @Nullable
    private final Boolean acknowledgeRequired;

    @Nullable
    private final Boolean deviceLimitBreached;

    @Nullable
    private final String deviceToken;

    @Nullable
    private final Long expiresIn;

    @Nullable
    private final String mobileNumber;

    @Nullable
    private final String refreshToken;

    @Nullable
    private final String tokenId;

    @Nullable
    private final UserDto user;

    /* renamed from: Companion, reason: from kotlin metadata */
    @NotNull
    public static final Companion INSTANCE = new Companion();
    public static final int $stable = 8;

    @aos
    public static final /* synthetic */ class a implements gy10<LoginDataDto> {

        @NotNull
        public static final a a;

        @NotNull
        private static final SerialDescriptor descriptor;

        static {
            a aVar = new a();
            a = aVar;
            s6s0 s6s0Var = new s6s0("com.penpencil.loginsignupkmp.data.dto.LoginDataDto", aVar, 10);
            s6s0Var.k("access_token", true);
            s6s0Var.k("refresh_token", true);
            s6s0Var.k("expires_in", true);
            s6s0Var.k("tokenId", true);
            s6s0Var.k("user", true);
            s6s0Var.k("ackRequired", true);
            s6s0Var.k("ackData", true);
            s6s0Var.k("deviceLimitBreached", true);
            s6s0Var.k("deviceToken", true);
            s6s0Var.k("mobileNumber", true);
            descriptor = s6s0Var;
        }

        @NotNull
        public final KSerializer<?>[] childSerializers() {
            mz81 mz81Var = mz81.a;
            KSerializer<?> c = gic.c(mz81Var);
            KSerializer<?> c2 = gic.c(mz81Var);
            KSerializer<?> c3 = gic.c(dxe0.a);
            KSerializer<?> c4 = gic.c(mz81Var);
            KSerializer<?> c5 = gic.c(UserDto.a.a);
            cyb cybVar = cyb.a;
            return new KSerializer[]{c, c2, c3, c4, c5, gic.c(cybVar), gic.c(PiracyAcknowledgeDataDto.a.a), gic.c(cybVar), gic.c(mz81Var), gic.c(mz81Var)};
        }

        public final Object deserialize(Decoder decoder) {
            Intrinsics.checkNotNullParameter(decoder, "decoder");
            SerialDescriptor serialDescriptor = descriptor;
            f6o c = decoder.c(serialDescriptor);
            String str = null;
            String str2 = null;
            String str3 = null;
            Long l = null;
            String str4 = null;
            UserDto userDto = null;
            Boolean bool = null;
            PiracyAcknowledgeDataDto piracyAcknowledgeDataDto = null;
            Boolean bool2 = null;
            String str5 = null;
            boolean z = true;
            int i = 0;
            while (z) {
                int w = c.w(serialDescriptor);
                switch (w) {
                    case Constants.UID_NOT_SET /* -1 */:
                        z = false;
                        break;
                    case Constants.$stable /* 0 */:
                        str2 = (String) c.t(serialDescriptor, 0, mz81.a, str2);
                        i |= 1;
                        break;
                    case 1:
                        str3 = (String) c.t(serialDescriptor, 1, mz81.a, str3);
                        i |= 2;
                        break;
                    case 2:
                        l = (Long) c.t(serialDescriptor, 2, dxe0.a, l);
                        i |= 4;
                        break;
                    case UnzipResult.Failure.FAILED_TO_PREPARE_OUTPUT_DIRECTORY /* 3 */:
                        str4 = (String) c.t(serialDescriptor, 3, mz81.a, str4);
                        i |= 8;
                        break;
                    case UnzipResult.Failure.UNZIP_FAILED /* 4 */:
                        userDto = (UserDto) c.t(serialDescriptor, 4, UserDto.a.a, userDto);
                        i |= 16;
                        break;
                    case UnzipResult.Failure.UNZIP_FAILED_DUE_TO_FILE_MISSING /* 5 */:
                        bool = (Boolean) c.t(serialDescriptor, 5, cyb.a, bool);
                        i |= 32;
                        break;
                    case 6:
                        piracyAcknowledgeDataDto = (PiracyAcknowledgeDataDto) c.t(serialDescriptor, 6, PiracyAcknowledgeDataDto.a.a, piracyAcknowledgeDataDto);
                        i |= 64;
                        break;
                    case 7:
                        bool2 = (Boolean) c.t(serialDescriptor, 7, cyb.a, bool2);
                        i |= 128;
                        break;
                    case 8:
                        str5 = (String) c.t(serialDescriptor, 8, mz81.a, str5);
                        i |= 256;
                        break;
                    case 9:
                        str = (String) c.t(serialDescriptor, 9, mz81.a, str);
                        i |= 512;
                        break;
                    default:
                        throw new t5h1(w);
                }
            }
            c.d(serialDescriptor);
            return new LoginDataDto(i, str2, str3, l, str4, userDto, bool, piracyAcknowledgeDataDto, bool2, str5, str, (q741) null);
        }

        @NotNull
        public final SerialDescriptor getDescriptor() {
            return descriptor;
        }

        public final void serialize(Encoder encoder, Object obj) {
            LoginDataDto loginDataDto = (LoginDataDto) obj;
            Intrinsics.checkNotNullParameter(encoder, "encoder");
            Intrinsics.checkNotNullParameter(loginDataDto, "value");
            SerialDescriptor serialDescriptor = descriptor;
            h6o c = encoder.c(serialDescriptor);
            LoginDataDto.write$Self$login_signup_kmp_pwProdRelease(loginDataDto, c, serialDescriptor);
            c.d(serialDescriptor);
        }

        @NotNull
        public final /* bridge */ KSerializer<?>[] typeParametersSerializers() {
            return t6s0.a;
        }
    }

    /* renamed from: com.penpencil.loginsignupkmp.data.dto.LoginDataDto$b, reason: from kotlin metadata */
    public static final class Companion {
        @NotNull
        public final KSerializer<LoginDataDto> serializer() {
            return a.a;
        }
    }

    public LoginDataDto() {
        this((String) null, (String) null, (Long) null, (String) null, (UserDto) null, (Boolean) null, (PiracyAcknowledgeDataDto) null, (Boolean) null, (String) null, (String) null, 1023, (DefaultConstructorMarker) null);
    }

    public static /* synthetic */ LoginDataDto copy$default(LoginDataDto loginDataDto, String str, String str2, Long l, String str3, UserDto userDto, Boolean bool, PiracyAcknowledgeDataDto piracyAcknowledgeDataDto, Boolean bool2, String str4, String str5, int i, Object obj) {
        if ((i & 1) != 0) {
            str = loginDataDto.accessToken;
        }
        if ((i & 2) != 0) {
            str2 = loginDataDto.refreshToken;
        }
        if ((i & 4) != 0) {
            l = loginDataDto.expiresIn;
        }
        if ((i & 8) != 0) {
            str3 = loginDataDto.tokenId;
        }
        if ((i & 16) != 0) {
            userDto = loginDataDto.user;
        }
        if ((i & 32) != 0) {
            bool = loginDataDto.acknowledgeRequired;
        }
        if ((i & 64) != 0) {
            piracyAcknowledgeDataDto = loginDataDto.acknowledgeData;
        }
        if ((i & 128) != 0) {
            bool2 = loginDataDto.deviceLimitBreached;
        }
        if ((i & 256) != 0) {
            str4 = loginDataDto.deviceToken;
        }
        if ((i & 512) != 0) {
            str5 = loginDataDto.mobileNumber;
        }
        String str6 = str4;
        String str7 = str5;
        PiracyAcknowledgeDataDto piracyAcknowledgeDataDto2 = piracyAcknowledgeDataDto;
        Boolean bool3 = bool2;
        UserDto userDto2 = userDto;
        Boolean bool4 = bool;
        return loginDataDto.copy(str, str2, l, str3, userDto2, bool4, piracyAcknowledgeDataDto2, bool3, str6, str7);
    }

    public static /* synthetic */ void getAccessToken$annotations() {
    }

    public static /* synthetic */ void getAcknowledgeData$annotations() {
    }

    public static /* synthetic */ void getAcknowledgeRequired$annotations() {
    }

    public static /* synthetic */ void getDeviceLimitBreached$annotations() {
    }

    public static /* synthetic */ void getDeviceToken$annotations() {
    }

    public static /* synthetic */ void getExpiresIn$annotations() {
    }

    public static /* synthetic */ void getMobileNumber$annotations() {
    }

    public static /* synthetic */ void getRefreshToken$annotations() {
    }

    public static /* synthetic */ void getTokenId$annotations() {
    }

    public static /* synthetic */ void getUser$annotations() {
    }

    public static final /* synthetic */ void write$Self$login_signup_kmp_pwProdRelease(LoginDataDto self, h6o output, SerialDescriptor serialDesc) {
        if (output.q(serialDesc, 0) || self.accessToken != null) {
            output.i(serialDesc, 0, mz81.a, self.accessToken);
        }
        if (output.q(serialDesc, 1) || self.refreshToken != null) {
            output.i(serialDesc, 1, mz81.a, self.refreshToken);
        }
        if (output.q(serialDesc, 2) || self.expiresIn != null) {
            output.i(serialDesc, 2, dxe0.a, self.expiresIn);
        }
        if (output.q(serialDesc, 3) || self.tokenId != null) {
            output.i(serialDesc, 3, mz81.a, self.tokenId);
        }
        if (output.q(serialDesc, 4) || self.user != null) {
            output.i(serialDesc, 4, UserDto.a.a, self.user);
        }
        if (output.q(serialDesc, 5) || self.acknowledgeRequired != null) {
            output.i(serialDesc, 5, cyb.a, self.acknowledgeRequired);
        }
        if (output.q(serialDesc, 6) || self.acknowledgeData != null) {
            output.i(serialDesc, 6, PiracyAcknowledgeDataDto.a.a, self.acknowledgeData);
        }
        if (output.q(serialDesc, 7) || self.deviceLimitBreached != null) {
            output.i(serialDesc, 7, cyb.a, self.deviceLimitBreached);
        }
        if (output.q(serialDesc, 8) || self.deviceToken != null) {
            output.i(serialDesc, 8, mz81.a, self.deviceToken);
        }
        if (!output.q(serialDesc, 9) && self.mobileNumber == null) {
            return;
        }
        output.i(serialDesc, 9, mz81.a, self.mobileNumber);
    }

    @Nullable
    /* renamed from: component1, reason: from getter */
    public final String getAccessToken() {
        return this.accessToken;
    }

    @Nullable
    /* renamed from: component10, reason: from getter */
    public final String getMobileNumber() {
        return this.mobileNumber;
    }

    @Nullable
    /* renamed from: component2, reason: from getter */
    public final String getRefreshToken() {
        return this.refreshToken;
    }

    @Nullable
    /* renamed from: component3, reason: from getter */
    public final Long getExpiresIn() {
        return this.expiresIn;
    }

    @Nullable
    /* renamed from: component4, reason: from getter */
    public final String getTokenId() {
        return this.tokenId;
    }

    @Nullable
    /* renamed from: component5, reason: from getter */
    public final UserDto getUser() {
        return this.user;
    }

    @Nullable
    /* renamed from: component6, reason: from getter */
    public final Boolean getAcknowledgeRequired() {
        return this.acknowledgeRequired;
    }

    @Nullable
    /* renamed from: component7, reason: from getter */
    public final PiracyAcknowledgeDataDto getAcknowledgeData() {
        return this.acknowledgeData;
    }

    @Nullable
    /* renamed from: component8, reason: from getter */
    public final Boolean getDeviceLimitBreached() {
        return this.deviceLimitBreached;
    }

    @Nullable
    /* renamed from: component9, reason: from getter */
    public final String getDeviceToken() {
        return this.deviceToken;
    }

    @NotNull
    public final LoginDataDto copy(@Nullable String accessToken, @Nullable String refreshToken, @Nullable Long expiresIn, @Nullable String tokenId, @Nullable UserDto user, @Nullable Boolean acknowledgeRequired, @Nullable PiracyAcknowledgeDataDto acknowledgeData, @Nullable Boolean deviceLimitBreached, @Nullable String deviceToken, @Nullable String mobileNumber) {
        return new LoginDataDto(accessToken, refreshToken, expiresIn, tokenId, user, acknowledgeRequired, acknowledgeData, deviceLimitBreached, deviceToken, mobileNumber);
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof LoginDataDto)) {
            return false;
        }
        LoginDataDto loginDataDto = (LoginDataDto) other;
        return Intrinsics.f(this.accessToken, loginDataDto.accessToken) && Intrinsics.f(this.refreshToken, loginDataDto.refreshToken) && Intrinsics.f(this.expiresIn, loginDataDto.expiresIn) && Intrinsics.f(this.tokenId, loginDataDto.tokenId) && Intrinsics.f(this.user, loginDataDto.user) && Intrinsics.f(this.acknowledgeRequired, loginDataDto.acknowledgeRequired) && Intrinsics.f(this.acknowledgeData, loginDataDto.acknowledgeData) && Intrinsics.f(this.deviceLimitBreached, loginDataDto.deviceLimitBreached) && Intrinsics.f(this.deviceToken, loginDataDto.deviceToken) && Intrinsics.f(this.mobileNumber, loginDataDto.mobileNumber);
    }

    @Nullable
    public final String getAccessToken() {
        return this.accessToken;
    }

    @Nullable
    public final PiracyAcknowledgeDataDto getAcknowledgeData() {
        return this.acknowledgeData;
    }

    @Nullable
    public final Boolean getAcknowledgeRequired() {
        return this.acknowledgeRequired;
    }

    @Nullable
    public final Boolean getDeviceLimitBreached() {
        return this.deviceLimitBreached;
    }

    @Nullable
    public final String getDeviceToken() {
        return this.deviceToken;
    }

    @Nullable
    public final Long getExpiresIn() {
        return this.expiresIn;
    }

    @Nullable
    public final String getMobileNumber() {
        return this.mobileNumber;
    }

    @Nullable
    public final String getRefreshToken() {
        return this.refreshToken;
    }

    @Nullable
    public final String getTokenId() {
        return this.tokenId;
    }

    @Nullable
    public final UserDto getUser() {
        return this.user;
    }

    public int hashCode() {
        String str = this.accessToken;
        int hashCode = (str == null ? 0 : str.hashCode()) * 31;
        String str2 = this.refreshToken;
        int hashCode2 = (hashCode + (str2 == null ? 0 : str2.hashCode())) * 31;
        Long l = this.expiresIn;
        int hashCode3 = (hashCode2 + (l == null ? 0 : l.hashCode())) * 31;
        String str3 = this.tokenId;
        int hashCode4 = (hashCode3 + (str3 == null ? 0 : str3.hashCode())) * 31;
        UserDto userDto = this.user;
        int hashCode5 = (hashCode4 + (userDto == null ? 0 : userDto.hashCode())) * 31;
        Boolean bool = this.acknowledgeRequired;
        int hashCode6 = (hashCode5 + (bool == null ? 0 : bool.hashCode())) * 31;
        PiracyAcknowledgeDataDto piracyAcknowledgeDataDto = this.acknowledgeData;
        int hashCode7 = (hashCode6 + (piracyAcknowledgeDataDto == null ? 0 : piracyAcknowledgeDataDto.hashCode())) * 31;
        Boolean bool2 = this.deviceLimitBreached;
        int hashCode8 = (hashCode7 + (bool2 == null ? 0 : bool2.hashCode())) * 31;
        String str4 = this.deviceToken;
        int hashCode9 = (hashCode8 + (str4 == null ? 0 : str4.hashCode())) * 31;
        String str5 = this.mobileNumber;
        return hashCode9 + (str5 != null ? str5.hashCode() : 0);
    }

    @NotNull
    public String toString() {
        String str = this.accessToken;
        String str2 = this.refreshToken;
        Long l = this.expiresIn;
        String str3 = this.tokenId;
        UserDto userDto = this.user;
        Boolean bool = this.acknowledgeRequired;
        PiracyAcknowledgeDataDto piracyAcknowledgeDataDto = this.acknowledgeData;
        Boolean bool2 = this.deviceLimitBreached;
        String str4 = this.deviceToken;
        String str5 = this.mobileNumber;
        StringBuilder a2 = ls00.a("LoginDataDto(accessToken=", str, ", refreshToken=", str2, ", expiresIn=");
        z3r.a(l, ", tokenId=", str3, ", user=", a2);
        a2.append(userDto);
        a2.append(", acknowledgeRequired=");
        a2.append(bool);
        a2.append(", acknowledgeData=");
        a2.append(piracyAcknowledgeDataDto);
        a2.append(", deviceLimitBreached=");
        a2.append(bool2);
        a2.append(", deviceToken=");
        return gc10.a(a2, str4, ", mobileNumber=", str5, ")");
    }

    public /* synthetic */ LoginDataDto(int i, String str, String str2, Long l, String str3, UserDto userDto, Boolean bool, PiracyAcknowledgeDataDto piracyAcknowledgeDataDto, Boolean bool2, String str4, String str5, q741 q741Var) {
        if ((i & 1) == 0) {
            this.accessToken = null;
        } else {
            this.accessToken = str;
        }
        if ((i & 2) == 0) {
            this.refreshToken = null;
        } else {
            this.refreshToken = str2;
        }
        if ((i & 4) == 0) {
            this.expiresIn = null;
        } else {
            this.expiresIn = l;
        }
        if ((i & 8) == 0) {
            this.tokenId = null;
        } else {
            this.tokenId = str3;
        }
        if ((i & 16) == 0) {
            this.user = null;
        } else {
            this.user = userDto;
        }
        if ((i & 32) == 0) {
            this.acknowledgeRequired = null;
        } else {
            this.acknowledgeRequired = bool;
        }
        if ((i & 64) == 0) {
            this.acknowledgeData = null;
        } else {
            this.acknowledgeData = piracyAcknowledgeDataDto;
        }
        if ((i & 128) == 0) {
            this.deviceLimitBreached = null;
        } else {
            this.deviceLimitBreached = bool2;
        }
        if ((i & 256) == 0) {
            this.deviceToken = null;
        } else {
            this.deviceToken = str4;
        }
        if ((i & 512) == 0) {
            this.mobileNumber = null;
        } else {
            this.mobileNumber = str5;
        }
    }

    public LoginDataDto(@Nullable String str, @Nullable String str2, @Nullable Long l, @Nullable String str3, @Nullable UserDto userDto, @Nullable Boolean bool, @Nullable PiracyAcknowledgeDataDto piracyAcknowledgeDataDto, @Nullable Boolean bool2, @Nullable String str4, @Nullable String str5) {
        this.accessToken = str;
        this.refreshToken = str2;
        this.expiresIn = l;
        this.tokenId = str3;
        this.user = userDto;
        this.acknowledgeRequired = bool;
        this.acknowledgeData = piracyAcknowledgeDataDto;
        this.deviceLimitBreached = bool2;
        this.deviceToken = str4;
        this.mobileNumber = str5;
    }

    public /* synthetic */ LoginDataDto(String str, String str2, Long l, String str3, UserDto userDto, Boolean bool, PiracyAcknowledgeDataDto piracyAcknowledgeDataDto, Boolean bool2, String str4, String str5, int i, DefaultConstructorMarker defaultConstructorMarker) {
        this((i & 1) != 0 ? null : str, (i & 2) != 0 ? null : str2, (i & 4) != 0 ? null : l, (i & 8) != 0 ? null : str3, (i & 16) != 0 ? null : userDto, (i & 32) != 0 ? null : bool, (i & 64) != 0 ? null : piracyAcknowledgeDataDto, (i & 128) != 0 ? null : bool2, (i & 256) != 0 ? null : str4, (i & 512) != 0 ? null : str5);
    }
}
