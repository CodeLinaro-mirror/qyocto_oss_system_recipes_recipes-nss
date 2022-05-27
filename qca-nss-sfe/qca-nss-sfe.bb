DESCRIPTION = "Shortcut forward engine(SFE) Shortcut is an in-Linux-kernel IP packet forwarding engine"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/${LICENSE};md5=f3b90e78ea0cffb20bf5cca7947a896d"

inherit module

FILESPATH =+ "${TOPDIR}/../opensource/:"
FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

FILES_${PN} += "/usr/bin"

SRC_URI = "file://qca-nss-sfe/ \
	   file://sfe_dump \
	   "
DEPENDS = "virtual/kernel"

S = "${WORKDIR}/qca-nss-sfe"

PACKAGES += "kernel-module-qca-nss-sfe"

EXTRA_CFLAGS += "-I${S}/exports"
SFE_MAKE_OPTS = "SFE_SUPPORT_IPV6=y"

do_configure() {
	true
}

do_compile() {
	unset LDFLAGS
	make -C "${STAGING_KERNEL_BUILDDIR}" \
		CROSS_COMPILE="${TARGET_PREFIX}" \
		ARCH="${KARCH}" \
		M="${S}" \
		EXTRA_CFLAGS="${EXTRA_CFLAGS}" \
		${SFE_MAKE_OPTS} \
		modules
}

do_install() {
	install -d ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}
	install -m 0644 qca-nss-sfe${KERNEL_OBJECT_SUFFIX} ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}
	install -d ${D}/usr/bin
	install -m 0755 ${WORKDIR}/sfe_dump ${D}/usr/bin
	install -d ${D}${includedir}/qca-nss-sfe
	install -m 0644 exports/* ${D}${includedir}/qca-nss-sfe/
	install -m 0644 ${S}/Module.symvers ${D}${includedir}/qca-nss-sfe/Module.symvers
}
