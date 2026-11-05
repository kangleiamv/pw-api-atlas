package com.penpencil.loginsignupkmp.data.dto;

import com.penpencil.live_class.presentation.utils.Constants;
import com.penpencil.loginsignupkmp.data.dto.ErrorDto;
import com.penpencil.loginsignupkmp.data.dto.LoginDataDto;
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

@Metadata(d1 = {"\u0000F\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\r\n\u0002\u0010\u000e\n\u0002\b\u0018\b\u0087\b\u0018\u0000 82\u00020\u0001:\u00029:B5\u0012\b\b\u0002\u0010\u0003\u001a\u00020\u0002\u0012\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u0004\u0012\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\u0006\u0012\n\b\u0002\u0010\t\u001a\u0004\u0018\u00010\b¢\u0006\u0004\b\n\u0010\u000bBA\b\u0010\u0012\u0006\u0010\f\u001a\u00020\b\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\b\u0010\u0005\u001a\u0004\u0018\u00010\u0004\u0012\b\u0010\u0007\u001a\u0004\u0018\u00010\u0006\u0012\b\u0010\t\u001a\u0004\u0018\u00010\b\u0012\b\u0010\u000e\u001a\u0004\u0018\u00010\r¢\u0006\u0004\b\n\u0010\u000fJ'\u0010\u0018\u001a\u00020\u00152\u0006\u0010\u0010\u001a\u00020\u00002\u0006\u0010\u0012\u001a\u00020\u00112\u0006\u0010\u0014\u001a\u00020\u0013H\u0001¢\u0006\u0004\b\u0016\u0010\u0017J\u0010\u0010\u0019\u001a\u00020\u0002HÆ\u0003¢\u0006\u0004\b\u0019\u0010\u001aJ\u0012\u0010\u001b\u001a\u0004\u0018\u00010\u0004HÆ\u0003¢\u0006\u0004\b\u001b\u0010\u001cJ\u0012\u0010\u001d\u001a\u0004\u0018\u00010\u0006HÆ\u0003¢\u0006\u0004\b\u001d\u0010\u001eJ\u0012\u0010\u001f\u001a\u0004\u0018\u00010\bHÆ\u0003¢\u0006\u0004\b\u001f\u0010 J>\u0010!\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00022\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u00042\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\u00062\n\b\u0002\u0010\t\u001a\u0004\u0018\u00010\bHÆ\u0001¢\u0006\u0004\b!\u0010\"J\u0010\u0010$\u001a\u00020#HÖ\u0001¢\u0006\u0004\b$\u0010%J\u0010\u0010&\u001a\u00020\bHÖ\u0001¢\u0006\u0004\b&\u0010'J\u001a\u0010)\u001a\u00020\u00022\b\u0010(\u001a\u0004\u0018\u00010\u0001HÖ\u0003¢\u0006\u0004\b)\u0010*R \u0010\u0003\u001a\u00020\u00028\u0006X\u0087\u0004¢\u0006\u0012\n\u0004\b\u0003\u0010+\u0012\u0004\b-\u0010.\u001a\u0004\b,\u0010\u001aR\"\u0010\u0005\u001a\u0004\u0018\u00010\u00048\u0006X\u0087\u0004¢\u0006\u0012\n\u0004\b\u0005\u0010/\u0012\u0004\b1\u0010.\u001a\u0004\b0\u0010\u001cR\"\u0010\u0007\u001a\u0004\u0018\u00010\u00068\u0006X\u0087\u0004¢\u0006\u0012\n\u0004\b\u0007\u00102\u0012\u0004\b4\u0010.\u001a\u0004\b3\u0010\u001eR\"\u0010\t\u001a\u0004\u0018\u00010\b8\u0006X\u0087\u0004¢\u0006\u0012\n\u0004\b\t\u00105\u0012\u0004\b7\u0010.\u001a\u0004\b6\u0010 ¨\u0006;"}, d2 = {"Lcom/penpencil/loginsignupkmp/data/dto/LoginResponseDto;", "", "", "success", "Lcom/penpencil/loginsignupkmp/data/dto/LoginDataDto;", "data", "Lcom/penpencil/loginsignupkmp/data/dto/ErrorDto;", "error", "", "status", "<init>", "(ZLcom/penpencil/loginsignupkmp/data/dto/LoginDataDto;Lcom/penpencil/loginsignupkmp/data/dto/ErrorDto;Ljava/lang/Integer;)V", "seen0", "Lq741;", "serializationConstructorMarker", "(IZLcom/penpencil/loginsignupkmp/data/dto/LoginDataDto;Lcom/penpencil/loginsignupkmp/data/dto/ErrorDto;Ljava/lang/Integer;Lq741;)V", "self", "Lh6o;", "output", "Lkotlinx/serialization/descriptors/SerialDescriptor;", "serialDesc", "", "write$Self$login_signup_kmp_pwProdRelease", "(Lcom/penpencil/loginsignupkmp/data/dto/LoginResponseDto;Lh6o;Lkotlinx/serialization/descriptors/SerialDescriptor;)V", "write$Self", "component1", "()Z", "component2", "()Lcom/penpencil/loginsignupkmp/data/dto/LoginDataDto;", "component3", "()Lcom/penpencil/loginsignupkmp/data/dto/ErrorDto;", "component4", "()Ljava/lang/Integer;", "copy", "(ZLcom/penpencil/loginsignupkmp/data/dto/LoginDataDto;Lcom/penpencil/loginsignupkmp/data/dto/ErrorDto;Ljava/lang/Integer;)Lcom/penpencil/loginsignupkmp/data/dto/LoginResponseDto;", "", "toString", "()Ljava/lang/String;", "hashCode", "()I", "other", "equals", "(Ljava/lang/Object;)Z", "Z", "getSuccess", "getSuccess$annotations", "()V", "Lcom/penpencil/loginsignupkmp/data/dto/LoginDataDto;", "getData", "getData$annotations", "Lcom/penpencil/loginsignupkmp/data/dto/ErrorDto;", "getError", "getError$annotations", "Ljava/lang/Integer;", "getStatus", "getStatus$annotations", "Companion", "a", "b", "login-signup-kmp_pwProdRelease"}, k = 1, mv = {2, UnzipResult.Failure.FAILED_TO_PREPARE_OUTPUT_DIRECTORY, Constants.$stable}, xi = 48)
@l741
/* loaded from: /tmp/opencode/pwdex/classes18.dex */
public final /* data */ class LoginResponseDto {

    @Nullable
    private final LoginDataDto data;

    @Nullable
    private final ErrorDto error;

    @Nullable
    private final Integer status;
    private final boolean success;

    /* renamed from: Companion, reason: from kotlin metadata */
    @NotNull
    public static final Companion INSTANCE = new Companion();
    public static final int $stable = 8;

    @aos
    public static final /* synthetic */ class a implements gy10<LoginResponseDto> {

        @NotNull
        public static final a a;

        @NotNull
        private static final SerialDescriptor descriptor;

        static {
            a aVar = new a();
            a = aVar;
            s6s0 s6s0Var = new s6s0("com.penpencil.loginsignupkmp.data.dto.LoginResponseDto", aVar, 4);
            s6s0Var.k("success", true);
            s6s0Var.k("data", true);
            s6s0Var.k("error", true);
            s6s0Var.k("status", true);
            descriptor = s6s0Var;
        }

        @NotNull
        public final KSerializer<?>[] childSerializers() {
            return new KSerializer[]{cyb.a, gic.c(LoginDataDto.a.a), gic.c(ErrorDto.a.a), gic.c(l980.a)};
        }

        public final Object deserialize(Decoder decoder) {
            Intrinsics.checkNotNullParameter(decoder, "decoder");
            SerialDescriptor serialDescriptor = descriptor;
            f6o c = decoder.c(serialDescriptor);
            int i = 0;
            boolean z = false;
            LoginDataDto loginDataDto = null;
            ErrorDto errorDto = null;
            Integer num = null;
            boolean z2 = true;
            while (z2) {
                int w = c.w(serialDescriptor);
                if (w == -1) {
                    z2 = false;
                } else if (w == 0) {
                    z = c.C(serialDescriptor, 0);
                    i |= 1;
                } else if (w == 1) {
                    loginDataDto = (LoginDataDto) c.t(serialDescriptor, 1, LoginDataDto.a.a, loginDataDto);
                    i |= 2;
                } else if (w == 2) {
                    errorDto = (ErrorDto) c.t(serialDescriptor, 2, ErrorDto.a.a, errorDto);
                    i |= 4;
                } else {
                    if (w != 3) {
                        throw new t5h1(w);
                    }
                    num = (Integer) c.t(serialDescriptor, 3, l980.a, num);
                    i |= 8;
                }
            }
            c.d(serialDescriptor);
            return new LoginResponseDto(i, z, loginDataDto, errorDto, num, (q741) null);
        }

        @NotNull
        public final SerialDescriptor getDescriptor() {
            return descriptor;
        }

        public final void serialize(Encoder encoder, Object obj) {
            LoginResponseDto loginResponseDto = (LoginResponseDto) obj;
            Intrinsics.checkNotNullParameter(encoder, "encoder");
            Intrinsics.checkNotNullParameter(loginResponseDto, "value");
            SerialDescriptor serialDescriptor = descriptor;
            h6o c = encoder.c(serialDescriptor);
            LoginResponseDto.write$Self$login_signup_kmp_pwProdRelease(loginResponseDto, c, serialDescriptor);
            c.d(serialDescriptor);
        }

        @NotNull
        public final /* bridge */ KSerializer<?>[] typeParametersSerializers() {
            return t6s0.a;
        }
    }

    /* renamed from: com.penpencil.loginsignupkmp.data.dto.LoginResponseDto$b, reason: from kotlin metadata */
    public static final class Companion {
        @NotNull
        public final KSerializer<LoginResponseDto> serializer() {
            return a.a;
        }
    }

    public LoginResponseDto() {
        this(false, (LoginDataDto) null, (ErrorDto) null, (Integer) null, 15, (DefaultConstructorMarker) null);
    }

    public static /* synthetic */ LoginResponseDto copy$default(LoginResponseDto loginResponseDto, boolean z, LoginDataDto loginDataDto, ErrorDto errorDto, Integer num, int i, Object obj) {
        if ((i & 1) != 0) {
            z = loginResponseDto.success;
        }
        if ((i & 2) != 0) {
            loginDataDto = loginResponseDto.data;
        }
        if ((i & 4) != 0) {
            errorDto = loginResponseDto.error;
        }
        if ((i & 8) != 0) {
            num = loginResponseDto.status;
        }
        return loginResponseDto.copy(z, loginDataDto, errorDto, num);
    }

    public static /* synthetic */ void getData$annotations() {
    }

    public static /* synthetic */ void getError$annotations() {
    }

    public static /* synthetic */ void getStatus$annotations() {
    }

    public static /* synthetic */ void getSuccess$annotations() {
    }

    public static final /* synthetic */ void write$Self$login_signup_kmp_pwProdRelease(LoginResponseDto self, h6o output, SerialDescriptor serialDesc) {
        if (output.q(serialDesc, 0) || self.success) {
            output.n(serialDesc, 0, self.success);
        }
        if (output.q(serialDesc, 1) || self.data != null) {
            output.i(serialDesc, 1, LoginDataDto.a.a, self.data);
        }
        if (output.q(serialDesc, 2) || self.error != null) {
            output.i(serialDesc, 2, ErrorDto.a.a, self.error);
        }
        if (!output.q(serialDesc, 3) && self.status == null) {
            return;
        }
        output.i(serialDesc, 3, l980.a, self.status);
    }

    /* renamed from: component1, reason: from getter */
    public final boolean getSuccess() {
        return this.success;
    }

    @Nullable
    /* renamed from: component2, reason: from getter */
    public final LoginDataDto getData() {
        return this.data;
    }

    @Nullable
    /* renamed from: component3, reason: from getter */
    public final ErrorDto getError() {
        return this.error;
    }

    @Nullable
    /* renamed from: component4, reason: from getter */
    public final Integer getStatus() {
        return this.status;
    }

    @NotNull
    public final LoginResponseDto copy(boolean success, @Nullable LoginDataDto data, @Nullable ErrorDto error, @Nullable Integer status) {
        return new LoginResponseDto(success, data, error, status);
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof LoginResponseDto)) {
            return false;
        }
        LoginResponseDto loginResponseDto = (LoginResponseDto) other;
        return this.success == loginResponseDto.success && Intrinsics.f(this.data, loginResponseDto.data) && Intrinsics.f(this.error, loginResponseDto.error) && Intrinsics.f(this.status, loginResponseDto.status);
    }

    @Nullable
    public final LoginDataDto getData() {
        return this.data;
    }

    @Nullable
    public final ErrorDto getError() {
        return this.error;
    }

    @Nullable
    public final Integer getStatus() {
        return this.status;
    }

    public final boolean getSuccess() {
        return this.success;
    }

    public int hashCode() {
        int i = (this.success ? 1231 : 1237) * 31;
        LoginDataDto loginDataDto = this.data;
        int hashCode = (i + (loginDataDto == null ? 0 : loginDataDto.hashCode())) * 31;
        ErrorDto errorDto = this.error;
        int hashCode2 = (hashCode + (errorDto == null ? 0 : errorDto.hashCode())) * 31;
        Integer num = this.status;
        return hashCode2 + (num != null ? num.hashCode() : 0);
    }

    @NotNull
    public String toString() {
        return "LoginResponseDto(success=" + this.success + ", data=" + this.data + ", error=" + this.error + ", status=" + this.status + ")";
    }

    public /* synthetic */ LoginResponseDto(int i, boolean z, LoginDataDto loginDataDto, ErrorDto errorDto, Integer num, q741 q741Var) {
        this.success = (i & 1) == 0 ? false : z;
        if ((i & 2) == 0) {
            this.data = null;
        } else {
            this.data = loginDataDto;
        }
        if ((i & 4) == 0) {
            this.error = null;
        } else {
            this.error = errorDto;
        }
        if ((i & 8) == 0) {
            this.status = null;
        } else {
            this.status = num;
        }
    }

    public LoginResponseDto(boolean z, @Nullable LoginDataDto loginDataDto, @Nullable ErrorDto errorDto, @Nullable Integer num) {
        this.success = z;
        this.data = loginDataDto;
        this.error = errorDto;
        this.status = num;
    }

    public /* synthetic */ LoginResponseDto(boolean z, LoginDataDto loginDataDto, ErrorDto errorDto, Integer num, int i, DefaultConstructorMarker defaultConstructorMarker) {
        this((i & 1) != 0 ? false : z, (i & 2) != 0 ? null : loginDataDto, (i & 4) != 0 ? null : errorDto, (i & 8) != 0 ? null : num);
    }
}
