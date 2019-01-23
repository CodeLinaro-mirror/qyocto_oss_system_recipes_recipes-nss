DESCRIPTION = "Shortcut forward engine(SFE) simulated driver"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/${LICENSE};md5=f3b90e78ea0cffb20bf5cca7947a896d"

inherit module

FILESPATH =+ "${TOPDIR}/../opensource/:"

SRC_URI = "file://shortcut-fe/ \
	   "

DEPENDS = "virtual/kernel"

S = "${WORKDIR}/shortcut-fe/simulated-driver"

PACKAGES += "kernel-module-shortcut-fe-drv"

EXTRA_OEMAKE += "TOOL_PATH='${STAGING_BINDIR_TOOLCHAIN}' \
		SYS_PATH='${STAGING_KERNEL_BUILDDIR}' \
		TOOLPREFIX='${TARGET_PREFIX}' \
		KVER='${KERNEL_VERSION}' \
		ARCH='${KARCH}' -j1 \
		"
EXTRA_CFLAGS += "-DSFE_SUPPORT_IPV6"

do_configure() {
	true
}

do_compile() {
	unset LDFLAGS
	make -C  "${STAGING_KERNEL_BUILDDIR}" \
		CROSS_COMPILE="${TARGET_PREFIX}" \
		ARCH="${KARCH}" \
		SUBDIRS="${S}" \
		EXTRA_CFLAGS="${EXTRA_CFLAGS}" \
		modules
}

do_install() {
	install -d ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}
	install -m 0644 shortcut-fe-drv${KERNEL_OBJECT_SUFFIX} ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}
	install -d ${D}/${includedir}/shortcut-fe
	install -m 0644 ${WORKDIR}/shortcut-fe/simulated-driver/sfe_drv.h ${D}/${includedir}/shortcut-fe/
}

KERNEL_MODULE_AUTOLOAD += "shortcut-fe-drv"
module_autoload_shortcut-fe-drv = "shortcut-fe-drv"
