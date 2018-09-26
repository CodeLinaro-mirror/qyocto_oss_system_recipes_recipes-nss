DESCRIPTION = "Shortcut forward engine(SFE)"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/${LICENSE};md5=f3b90e78ea0cffb20bf5cca7947a896d"

inherit module

FILESPATH =+ "${TOPDIR}/../opensource/:"
FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

FILES_${PN} += "/usr/bin"

SRC_URI = "file://shortcut-fe/ \
	   file://sfe_dump \
	   "
DEPENDS = "virtual/kernel simulated-driver"

S = "${WORKDIR}/shortcut-fe/shortcut-fe"

PACKAGES += "kernel-module-shortcut-fe kernel-module-shortcut-fe-ipv6"

EXTRA_OEMAKE += "TOOL_PATH='${STAGING_BINDIR_TOOLCHAIN}' \
		SYS_PATH='${STAGING_KERNEL_BUILDDIR}' \
		TOOLPREFIX='${TARGET_PREFIX}' \
		KVER='${KERNEL_VERSION}' \
		ARCH='arm' -j1 \
		"
EXTRA_CFLAGS += "-DSFE_SUPPORT_IPV6"

do_configure() {
	true
}

do_compile() {
	make -C  "${STAGING_KERNEL_BUILDDIR}" \
		CROSS_COMPILE="${TARGET_PREFIX}" \
		ARCH="arm" \
		SUBDIRS="${S}" \
		EXTRA_CFLAGS="${EXTRA_CFLAGS}" \
		SFE_SUPPORT_IPV6=1 \
		modules
}

do_install() {
	install -d ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}
	install -m 0644 shortcut-fe${KERNEL_OBJECT_SUFFIX} ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}
	install -m 0644 shortcut-fe-ipv6${KERNEL_OBJECT_SUFFIX} ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}
	install -m 0644 shortcut-fe-cm${KERNEL_OBJECT_SUFFIX} ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}
	install -d ${D}/usr/bin
	install -m 0755 ${WORKDIR}/sfe_dump ${D}/usr/bin
}

KERNEL_MODULE_AUTOLOAD += "shortcut-fe"
module_autoload_shortcut-fe = "shortcut-fe shortcut-fe-ipv6"
